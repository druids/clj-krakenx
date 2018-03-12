(ns clj-krakenx.core
  (:require
    [clojure.string :refer [blank? join]]
    [cemerick.url :as url]
    [clj-http.client :as http]
    [io.aviso.toolchest.collections :refer [update-if?]]))


(def comma-join (partial join ","))


(def http-opts
  {:accept :json
   :content-type :json
   :throw-exceptions false})


(def routes
  (reduce-kv #(assoc %1 %2 (str "/0/public/" %3)) {} {:time "Time"
                                                      :asset-info "Assets"
                                                      :tradable-asset-pairs "AssetPairs"
                                                      :ticker-info "Ticker"
                                                      :ohlc-data "OHLC"
                                                      :recent-trades "Trades"
                                                      :recent-spread-data "Spread"
                                                      :orderbook "Depth"}))


(defn- unmarshal-response
  "Unmashals a `response` as JSON. Because Kraken doesn't care about HTTP codes success and failure calls are returned
  with status code 200. A success result is parsed and assoced into `:krakenx/result` attribute and an error result
  into `:krakenx/error`. In case of invalid JSON response, nothing is parsed and attribute `:krakenx/unmarshal-error`
  is assoced within a parsed error message."
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


(defn get-asset-info
  "Returns asset information"
  ([]
   (get-asset-info nil))
  ([opts]
   (get-asset-info opts kraken-host))
  ([opts host]
   (post-request host (:asset-info routes) (update-if? opts :asset comma-join))))


(defn get-tradable-asset-pairs
  "Returns tradable asset pairs"
  ([]
   (get-tradable-asset-pairs nil))
  ([opts]
   (get-tradable-asset-pairs opts kraken-host))
  ([opts host]
   (post-request host (:tradable-asset-pairs routes) (update-if? opts :pair comma-join))))


(defn get-ticker-info
  "Returns ticker information"
  ([opts]
   (get-ticker-info opts kraken-host))
  ([opts host]
   (post-request host (:ticker-info routes) (update opts :pair comma-join))))
