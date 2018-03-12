(ns clj-krakenx-test.get-ohlc-data-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-ohlc-data-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-ohlc-data-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XXBTZEUR [[1520812980 "7758.0" "7760.0" "7756.1" "7758.5" "7757.5" "0.6066" 13]
                                                [1520813040 "7758.5" "7770.0" "7757.1" "7770.0" "7761.4" "1.9496" 14]]}}
          error-response {:error ["EQuery:Unknown asset"]}]
      (are [response result error pair]
           (with-fake-routes (json-handler (:ohlc-data krakenx/routes) response)
             (assert-ohlc-data-response (krakenx/get-ohlc-data {:pair pair} krakenx-host) result error))

           success-response (:result success-response) (:error success-response) "BTCEUR"
           error-response (:result error-response) (:error error-response) "foo")))

  (testing "default host, with options"
    (let [response {:error []
                    :result {:XXBTZEUR [[1520812980 "7758.0" "7760.0" "7756.1" "7758.5" "7757.5" "0.6066" 13]
                                        [1520813040 "7758.5" "7770.0" "7757.1" "7770.0" "7761.4" "1.9496" 14]]}}]
      (with-fake-routes {(str krakenx/kraken-host (:ohlc-data krakenx/routes))
                         {:post (fn [_]
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-ohlc-data-response (krakenx/get-ohlc-data {:pair "BTCEUR"}) (:result response) [])))))
