(ns clj-krakenx-test.get-ticker-info-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-ticker-info-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-ticker-info-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XXBTZEUR {:a ["7970.70000" "1" "1.000"]}}}
          error-response {:error ["EQuery:Unknown pair"]}]
      (are [response result error pair]
           (with-fake-routes (json-handler (:ticker-info krakenx/routes) response)
             (assert-ticker-info-response (krakenx/get-ticker-info {:pair pair} krakenx-host)
                                          result
                                          error))

           success-response (:result success-response) (:error success-response) ["ZEUR" "XETH"]
           error-response (:result error-response) (:error error-response) ["foo"])))

  (testing "default host, with options"
    (let [response {:error []
                    :result {:XXBTZEUR {:a ["7970.70000" "1" "1.000"]}}}]
      (with-fake-routes {(str krakenx/kraken-host (:ticker-info krakenx/routes))
                         {:post (fn [request]
                                  (is(= "{\"pair\":\"XXBTZEUR,ETHEUR\"}" (-> request :body slurp)))
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-ticker-info-response (krakenx/get-ticker-info {:pair ["XXBTZEUR" "ETHEUR"]}) (:result response) [])))))
