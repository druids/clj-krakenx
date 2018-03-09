clj-krakenx
===========

A client for [Kraken Bitcoin Exchange API](https://www.kraken.com/help/api) based on [clj-http.client](https://clojars.org/clj-http).

[![CircleCI](https://circleci.com/gh/druids/clj-krakenx.svg?style=svg)](https://circleci.com/gh/druids/clj-krakenx)
[![Dependencies Status](https://jarkeeper.com/druids/clj-krakenx/status.png)](https://jarkeeper.com/druids/clj-krakenx)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------

```clojure
[clj-krakenx "0.0.0"]
```


Documentation
-------------

All functions are designed to return errors instead of throwing exceptions (except `:pre` in a function).

All API calls return an original HTTP response with another keys:
- `:krakenx/result` a parsed success response
- `:krakenx/error` a parsed error response
- `:krakenx/unmarshal-error` an error message that occurred during unmarshaling invalid JSON

To be able to run examples this line is needed:

```clojure
(require '[clj-krakenx.core :as krakenx])
```

### get-time

Returns server's time

```clojure
(:krakenx/result (krakenx/get-time)) ;; {:unixtime 1520605639, :rfc1123 "Fri,  9 Mar 18 14:27:19 +0000"}
```

When you need to mock a response, you can pass a host to a caller

```clojure
(krakenx/get-time "my-mock-domain.localhost")
```


### get-asset-info

Returns asset information. Request's options can be passed as hash-map. Assets are joined by a comma automatically,
thus you don't need to do it by a hand.

```clojure
(:krakenx/result (krakenx/get-asset-info {:asset ["ZEUR" "XETH"]}))
;; {:XETH {:aclass "currency", :altname "ETH", :decimals 10, :display_decimals 5},
;;  :ZEUR {:aclass "currency", :altname "EUR", :decimals 4, :display_decimals 2}}
```

Error response example

```clojure
(:krakenx/error (krakenx/get-asset-info {:asset ["foo"]})) ;; ["EQuery:Unknown asset"]
```

Of source a host can by passed as last argument

```clojure
(krakenx/get-asset-info {:asset ["ZEUR" "XETH"]} "my-mock-domain.localhost"))
```
