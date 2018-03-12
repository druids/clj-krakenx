clj-krakenx
===========

A client for [Kraken Bitcoin Exchange API](https://www.kraken.com/help/api) based on [clj-http.client](https://clojars.org/clj-http).

[![CircleCI](https://circleci.com/gh/druids/clj-krakenx.svg?style=svg)](https://circleci.com/gh/druids/clj-krakenx)
[![Dependencies Status](https://jarkeeper.com/druids/clj-krakenx/status.png)](https://jarkeeper.com/druids/clj-krakenx)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------

```clojure
[clj-krakenx "0.1.0"]
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

Of course a host can by passed as last argument

```clojure
(krakenx/get-asset-info {:asset ["ZEUR" "XETH"]} "my-mock-domain.localhost"))
```


### get-tradable-asset-pairs

Returns tradable asset pairs. Request's options can be passed as hash-map. Pairs are joined by a comma automatically,
thus you don't need to do it by a hand.

To get all tradable asset pairs

```clojure
(:krakenx/result (krakenx/get-tradable-asset-pairs))
;; {:EOSETH {:lot_decimals 8, :altname "EOSETH", :aclass_base "currency", :margin_call 80, :leverage_buy [], ...
```

To get margin of XBT/EUR
```clojure
(:krakenx/result (krakenx/get-tradable-asset-pairs {:info "margin", :pair ["XXBTZEUR"]}))
;; {:XXBTZEUR {:margin_call 80, :margin_level 40}}
```

Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-tradable-asset-pairs {:info "margin", :pair ["XXBTZEUR"]} "my-mock-domain.localhost"))
;; {:XXBTZEUR {:margin_call 80, :margin_level 40}}
```

### get-ticker-information

Returns ticker information

```clojure
(:krakenx/result (krakenx/get-ticker-info {:pair ["BTCEUR"]}))
;; {:XXBTZEUR {:v ["4481.33532282" "13957.63622703"], :o "7748.60000", ...
```


```clojure
(:krakenx/result (krakenx/get-ticker-info {:pair ["BTCEUR"]}))
;; {:XXBTZEUR {:v ["4481.33532282" "13957.63622703"], :o "7748.60000", ...
```

Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-ohlc-data {:pair "BTCEUR"} "my-mock-domain.localhost"))
;; {:XXBTZEUR {:v ["4481.33532282" "13957.63622703"], :o "7748.60000", ...
```

### get-ohlc-data

Returns OHLC data

```clojure
(:krakenx/result (krakenx/get-ohlc-data {:pair "BTCEUR"}))
;; {:XXBTZEUR [[1519561800 "7817.4" "7817.4" "7727.2" "7757.9" "7767.6" "232.23015300" 1057] [1519563600 "7755.1" "7800.0" "7710.0" "7718.9" "7766.0" "223.74132895" 578], ... 
```

Get OHLC data within interval 30 minutes

```clojure
(:krakenx/result (krakenx/get-ohlc-data {:pair "BTCEUR", :interval 30}))
;; {:XXBTZEUR [[1519561800 "7817.4" "7817.4" "7727.2" "7757.9" "7767.6" "232.23015300" 1057] [1519563600 "7755.1" "7800.0" "7710.0" "7718.9" "7766.0" "223.74132895" 578], ... 
```


Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-ticker-info {:pair ["BTCEUR"]} "my-mock-domain.localhost"))
;; {:XXBTZEUR [[1519561800 "7817.4" "7817.4" "7727.2" "7757.9" "7767.6" "232.23015300" 1057] [1519563600 "7755.1" "7800.0" "7710.0" "7718.9" "7766.0" "223.74132895" 578], ... 
```

### get-orderbook

Returns recent trades

```clojure
(:krakenx/result (krakenx/get-orderbook {:pair "BTCEUR"}))
;; {:XXBTZEUR {:asks [["7969.50000" "0.159" 1520858436], ...
```

Get trade data within since ID

```clojure
(:krakenx/result (krakenx/get-orderbook {:pair "BTCEUR", :count 2}))
;; {:XXBTZEUR {:asks [["7961.50000" "0.034" 1520858689] ["7962.00000" "1.108" 1520858697]]
;;             :bids [["7959.90000" "0.013" 1520858696] ["7957.80000" "2.046" 1520858698]]}}
```


Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-orderbook {:pair "BTCEUR"} "my-mock-domain.localhost"))
;; {:XXBTZEUR {:asks [["7969.50000" "0.159" 1520858436], ...
```

### get-recent-trades

Returns recent trades

```clojure
(:krakenx/result (krakenx/get-recent-trades {:pair "BTCEUR"}))
;; {:XXBTZEUR [["7979.60000" "1.50000000" 1.5208551442189E9 "b" "m" ""], ...
```

Get trade data within since ID

```clojure
(:krakenx/result (krakenx/get-recent-trades {:pair "BTCEUR", :since 1504035462}))
;; {:XXBTZEUR [["7979.60000" "1.50000000" 1.5208551442189E9 "b" "m" ""], ...
```


Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-recent-trades {:pair "BTCEUR"} "my-mock-domain.localhost"))
;; {:XXBTZEUR [["7979.60000" "1.50000000" 1.5208551442189E9 "b" "m" ""], ...
```

### get-recent-spread-data

Returns recent spread data

```clojure
(:krakenx/result (krakenx/get-recent-spread-data {:pair "BTCEUR"}))
;; {:XXBTZEUR [[1520858621 "7949.10000" "7950.00000"], ...
```

Get spread data within since ID

```clojure
(:krakenx/result (krakenx/get-recent-spread-data {:pair "BTCEUR", :since 1504035462}))
;; {:XXBTZEUR [[1520858621 "7949.10000" "7950.00000"], ...


Of course a host can by passed as last argument

```clojure
(:krakenx/result (krakenx/get-recent-spread-data {:pair "BTCEUR"} "my-mock-domain.localhost"))
;; {:XXBTZEUR [[1520858621 "7949.10000" "7950.00000"], ...
```
