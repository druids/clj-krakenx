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

To be able to run examples these lines are needed:

```clojure
(require '[clj-krakenx.core :as krakenx])

(def host "https://api.kraken.com")
```
