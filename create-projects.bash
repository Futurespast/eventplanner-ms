#!/usr/bin/env bash

spring init \
--boot-version=3.2.3 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=events-service \
--package-name=com.eventplanner.events \
--groupId=com.eventplanner.events \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
events-service

spring init \
--boot-version=3.2.3 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=customers-service \
--package-name=com.eventplanner.customers \
--groupId=com.eventplanner.customers \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
customers-service

spring init \
--boot-version=3.2.3 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=venues-service \
--package-name=com.eventplanner.venues \
--groupId=com.eventplanner.venues \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
venues-service

spring init \
--boot-version=3.2.3 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=participants-service \
--package-name=com.eventplanner.participants \
--groupId=com.eventplanner.participants \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
participants-service

spring init \
--boot-version=3.2.3 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=api-gateway \
--package-name=com.eventplanner.apigateway \
--groupId=com.eventplanner.apigateway \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
api-gateway

