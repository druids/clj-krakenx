(ns clj-krakenx-test.get-orderbook-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-orderbook-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-orderbook-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XXBTZEUR {:asks [["7969.50000" "0.159" 1520858436]]}}}
          error-response {:error ["EQuery:Unknown error"]}]
      (are [response result error pair]
           (with-fake-routes (json-handler (:orderbook krakenx/routes) response)
             (assert-orderbook-response (krakenx/get-orderbook {:pair pair} krakenx-host) result error))

           success-response (:result success-response) (:error success-response) "BTCEUR"
           error-response (:result error-response) (:error error-response) "foo")))

  (testing "default host, with options"
    (let [response {:error []
                    :result {:XXBTZEUR {:asks [["7969.50000" "0.159" 1520858436]]}}}]
      (with-fake-routes {(str krakenx/kraken-host (:orderbook krakenx/routes))
                         {:post (fn [_]
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-orderbook-response (krakenx/get-orderbook {:pair "BTCEUR"}) (:result response) [])))))
