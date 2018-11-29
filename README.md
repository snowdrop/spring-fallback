[![CircleCI](https://circleci.com/gh/snowdrop/spring-fallback.svg?style=shield)](https://circleci.com/gh/snowdrop/spring-fallback)

[![Maven Central](https://img.shields.io/maven-central/v/me.snowdrop/spring-fallback.svg)](https://mvnrepository.com/artifact/me.snowdrop/spring-fallback/0.1.1)

## Purpose

The purpose of this project is to provide a simple `@Fallback` annotation that will provide
the ability to provide a fallback value for failed calls

## Use cases

A simple use case is to combine this annotation with Istio in order to forgo the need of having to introduce Hystix 
