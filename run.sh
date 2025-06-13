#!/bin/bash

# Clean previous builds
sbt clean

# Compile
sbt compile

# Run with proper resource loading
sbt "runMain com.ecommerce.Main"
