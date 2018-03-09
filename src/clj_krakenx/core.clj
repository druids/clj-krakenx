(ns clj-krakenx.core
  (:require
    [clojure.string :refer [blank? join]]
    [cemerick.url :as url]
    [clj-http.client :as http]))


(def http-opts
  {:accept :json
   :content-type :json
   :throw-exceptions false})


(def routes
  (reduce-kv #(assoc %1 %2 (str "/0/public/" %3)) {} {:time "Time"
                                                      :asset-info "Assets"
                                                      :tradable-asset-pair "AssetPairs"
                                                      :ticker-info "Ticker"
                                                      :ohlc-data "OHLC"
                                                      :recent-trades "Trades"
                                                      :recent-spread-data "Spread"
                                                      :orderbook "Depth"}))


(defn- unmarshal-response
  [response]
  (try
    (let [body (http/json-decode (:body response) true)]
      (-> response
          (assoc :krakenx/result (:result body))
          (assoc :krakenx/error (:error body))))
    (catch Exception e
      (assoc response :krakenx/unmarshal-error (.getMessage e)))))


(defn- post-request
  [host path opts]
  {:pre [(not (blank? host))]}
  (-> host
      str
      url/url
      (assoc :path path)
      str
      (http/post (merge http-opts (when (seq opts) {:form-params opts})))
      unmarshal-response))


(def kraken-host "https://api.kraken.com")


(defn get-time
  "Returns server's time"
  ([]
   (get-time kraken-host))
  ([host]
   (post-request host (:time routes) nil)))
