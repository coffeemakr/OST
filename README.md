# OST
An Open Swiss Transport (OST) android app.

[![Build Status](https://travis-ci.org/coffeemakr/OST.svg?branch=master)](https://travis-ci.org/coffeemakr/OST)
[![codecov](https://codecov.io/gh/coffeemakr/OST/branch/master/graph/badge.svg)](https://codecov.io/gh/coffeemakr/OST)

## Values

 * **Offline friendly**: 
    Let the user store connections offline to use them en route without internet.
 * **Privacy-enhanced**: 
    Don't send unnecesary information to tracking servers. Also send all requests encrypted.

## Service

OST tries to use free and open-source services. The user should choose what services is to be used.


### Station Autocompletion

For the autocompletion of the stations can be provided by the following services:
 
 * **Offline database:** OST has a built-in autocompletion database. No queries are sent to the internet.
 * **transport.opendata.ch:** Open-source service and relayed to timetable.search.ch. (See on [Github](https://github.com/OpendataCH/Transport))
 * **timetable.search.ch:** Free but closed-source service which is used as the information source for transport.opendata.ch.


### Connections

For connection queries currently only transport.opendata.ch API is supported.

### More information
  
 * [Documentation of timetable.search.ch API](https://timetable.search.ch/api/help)
 * [Documentation of transport](http://transport.opendata.ch/)