#!/usr/bin/env bash
#
# Sample usage:
#   ./test_all.bash start stop
#   start and stop are optional
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
# When not in Docker
#: ${HOST=localhost}
#: ${PORT=7000}

# When in Docker
: ${HOST=localhost}
: ${PORT=8080}

#array to hold all our test data ids
allTestCustomerIds=()
allTestEventIds=()

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

#have all the microservices come up yet?
function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

#prepare the test data that will be passed in the curl commands for posts and puts
function setupTestdata() {

##CREATE SOME CUSTOMER TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
#
body=\
'{
  "firstName":"Christine",
  "lastName":"Gerard",
  "emailAddress":"christine@gmail.com",
  "streetAddress": "99 Main Street",
  "city":"Montreal",
  "province": "Quebec",
  "country": "Canada",
  "postalCode": "H3A 1A1",
  "phoneNumbers": [
    {
      "type": "HOME",
      "number": "514-555-5555"
    },
    {
      "type": "WORK",
      "number": "514-555-5556"
    }
  ]
}'
    recreateCustomer 1 "$body"
#

} #end of setupTestdata

##CREATE SOME EVENT TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
#

body=\
' {

      "venueId": "8d996257-e535-4614-98f6-4596be2a3626",
      "customerId": "c3540a89-cb47-4c96-888e-ff96708db4d8",
      "participantIds": ["25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"],
      "eventDate": {
          "startDate": "2024-10-01",
          "endDate": "2024-10-01"
      },
      "eventStatus": "PLANNED",
      "eventName": "Concert",
      "description": "Concert performed by DJ Sam"
  }'

recreateEvent 1 "$body" "c3540a89-cb47-4c96-888e-ff96708db4d8"


#USING SALE TEST DATA - EXECUTE POST REQUEST
function recreateEvent() {
    local testId=$1
    local aggregate=$2
    local customerId=$3

    #create the sale and record the generated saleId
    saleId=$(curl -X POST http://$HOST:$PORT/api/v1/customers/$customerId/events -H "Content-Type:
    application/json" --data "$aggregate" | jq '.eventId')
    allTestEventIds[$testId]=$eventId
    echo "Added Customer with eventId: ${allTestEventIds[$testId]}"
}

#USING CUSTOMER TEST DATA - EXECUTE POST REQUEST
function recreateCustomer() {
    local testId=$1
    local aggregate=$2

    #create the customer and record the generated customerId
    customerId=$(curl -X POST http://$HOST:$PORT/api/v1/customers -H "Content-Type:
    application/json" --data "$aggregate" | jq '.customerId')
    allTestCustomerIds[$testId]=$customerId
    echo "Added Customer with customerId: ${allTestCustomerIds[$testId]}"
}


#don't start testing until all the microservices are up and running
function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

#start of test script
set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

#try to delete an entity/aggregate that you've set up but that you don't need. This will confirm that things are working
waitForService curl -X DELETE http://$HOST:$PORT/api/v1/customers/i9j0k1l2-m3n4-5o6p-7q8r-9s0t1u2v3w4x

setupTestdata

#EXECUTE EXPLICIT TESTS AND VALIDATE RESPONSE
#
##verify that a get all customers works
echo -e "\nTest 1: Verify that a get all customers works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/customers -s"
assertEqual 10 $(echo $RESPONSE | jq ". | length")
#
#
## Verify that a normal get by id of earlier posted customer works
echo -e "\nTest 2: Verify that a normal get by id of earlier posted customer works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/customers/${allTestCustomerIds[1]} '${body}' -s"
assertEqual ${allTestCustomerIds[1]} $(echo $RESPONSE | jq .customerId)
assertEqual "\"Christine\"" $(echo $RESPONSE | jq ".firstName")
#
#
## Verify that an update of an earlier posted customer works - put at api-gateway has no response body
echo -e "\nTest 3: Verify that an update of an earlier posted customer works"
body=\
'{
  "firstName":"Christine UPDATED",
  "lastName":"Gerard",
  "emailAddress":"christine@gmail.com",
  "streetAddress": "99 Main Street",
  "city":"Montreal",
  "province": "Quebec",
  "country": "Canada",
  "postalCode": "H3A 1A1",
  "phoneNumbers": [
    {
      "type": "HOME",
      "number": "514-555-5555"
    },
    {
      "type": "WORK",
      "number": "514-555-5556"
    }
  ]
}'
assertCurl 200 "curl -X PUT http://$HOST:$PORT/api/v1/customers/${allTestCustomerIds[1]} -H \"Content-Type: application/json\" -d '${body}' -s"
#
#
## Verify that a delete of an earlier posted customer works
echo -e "\nTest 4: Verify that a delete of an earlier posted customer works"
assertCurl 204 "curl -X DELETE http://$HOST:$PORT/api/v1/customers/${allTestCustomerIds[1]} -s"
#
#
## Verify that a 404 (Not Found) status is returned for a non existing customerId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 5: Verify that a 404 (Not Found) error is returned for a get customer request with a non existing customerId"
assertCurl 404 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d7 -s"
#
#
## Verify that a 422 (Unprocessable Entity) status is returned for an invalid customerId (c3540a89-cb47-4c96-888e-ff96708db4d)
echo -e "\nTest 6: Verify that a 422 (Unprocessable Entity) status is returned for a get customer request with an invalid customerId"
assertCurl 422 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d -s"


## verify getallCustomerPurchases works

echo  -e "n\Test 7: Verify that a get all events works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d8/events -s"
assertEqual 2 $(echo $RESPONSE | jq ".| length")

## verify that get purchases by id works
assertCurl 200 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d8/purchases/${allTestEventIds[1]} -s"
assertEqual ${allTestEventIds[1]} $(echo $RESPONSE | jq .eventId)
assertEqual "\"John\"" $(echo $RESPONSE | jq ".customerFirstName")
assertEqual "\"Venue 1\"" $(echo $RESPONSE | jq ".venueName")


## verify that an update of an earlier posted customer purchase works - put at api-gateway has no response body
body=\
'{

      "venueId": "7e62e2d8-3bca-4083-b563-c5704f38a44c",
      "customerId": "c3540a89-cb47-4c96-888e-ff96708db4d8",
      "participantIds": ["25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"],
      "eventDate": {
          "startDate": "2024-10-01",
          "endDate": "2024-10-01"
      },
      "eventStatus": "PLANNED",
      "eventName": "Concert",
      "description": "Concert performed by DJ Sam"
  }'

assertCurl 200 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d8/events/${allTestEventIds[1]} -H \"Content-Type: application/json\" -d '${body}' -s"
assertEqual ${allTestEventIds[1]} $(echo $RESPONSE | jq .eventId)
assertEqual "\"John\"" $(echo $RESPONSE | jq ".customerFirstName")
assertEqual "\"Venue 2\"" $(echo $RESPONSE | jq ".venueName")

## TODO: Add verification of vehicle status change to sold

## delete of a posted purchase
echo  -e "n\Test 9: Verify that a delete event works"
assertCurl 204 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d8/events/${allTestEventIds[1]} -s"
## verify that a 404 is returned for a non existing customer id
echo  -e "n\Test 10: Verify that a 404 for non existing customer id"
assertCurl 404 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db/events/${allTestEventIds[1]} -s"
## verify unprocessable entity for a non-existing event
echo  -e "n\Test 11: Verify that a non existing event returns 422"
assertCurl 422 "curl http://$HOST:$PORT/api/v1/customers/c3540a89-cb47-4c96-888e-ff96708db4d8/purchases/11123e2qs -s"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi