(ns clj-krakenx-test.get-recent-spread-data-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-recent-spread-data-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-recent-spread-data-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XXBTZEUR [[1520858621 "7949.10000" "7950.00000"]]}}
          error-response {:error ["EQuery:Unknown asset"]}]
      (are [response result error pair]
           (with-fake-routes (json-handler (:recent-spread-data krakenx/routes) response)
             (assert-recent-spread-data-response (krakenx/get-recent-spread-data {:pair pair} krakenx-host)
                                                 result
                                                 error))

           success-response (:result success-response) (:error success-response) "BTCEUR"
           error-response (:result error-response) (:error error-response) "foo")))

  (testing "default host, with options"
    (let [response {:error []
                    :result {:XXBTZEUR [[1520858621 "7949.10000" "7950.00000"]]}}]
      (with-fake-routes {(str krakenx/kraken-host (:recent-spread-data krakenx/routes))
                         {:post (fn [_]
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-recent-spread-data-response (krakenx/get-recent-spread-data {:pair "BTCEUR"}) (:result response) [])))))
