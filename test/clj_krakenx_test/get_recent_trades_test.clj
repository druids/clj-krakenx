(ns clj-krakenx-test.get-recent-trades-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-recent-trades-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-recent-trades-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XXBTZEUR [["7979.60000" "1.50000000" 1.5208551442189E9 "b" "m" ""]]}}
          error-response {:error ["EQuery:Unknown asset"]}]
      (are [response result error pair]
           (with-fake-routes (json-handler (:recent-trades krakenx/routes) response)
             (assert-recent-trades-response (krakenx/get-recent-trades {:pair pair} krakenx-host) result error))

           success-response (:result success-response) (:error success-response) "BTCEUR"
           error-response (:result error-response) (:error error-response) "foo")))

  (testing "default host, with options"
    (let [response {:error []
                    :result {:XXBTZEUR [["7979.60000" "1.50000000" 1.5208551442189E9 "b" "m" ""]]}}]
      (with-fake-routes {(str krakenx/kraken-host (:recent-trades krakenx/routes))
                         {:post (fn [_]
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-recent-trades-response (krakenx/get-recent-trades {:pair "BTCEUR"}) (:result response) [])))))
