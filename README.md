[![CircleCI](https://circleci.com/gh/snowdrop/spring-fallback.svg?style=shield)](https://circleci.com/gh/snowdrop/spring-fallback)

## Purpose

The purpose of this project is to provide a simple `@Fallback` annotation that will provide
the ability to provide a fallback value for failed calls

## Use cases

A simple use case is to combine this annotation with Istio in order to forgo the need of having to introduce Hystix 
