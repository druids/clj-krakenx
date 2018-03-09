(ns clj-krakenx-test.get-asset-info-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-asset-info-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-asset-info-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XETH {:aclass "currency", :altname "ETH", :decimals 10, :display_decimals 5}
                                     :ZEUR {:aclass "currency", :altname "EUR", :decimals 4, :display_decimals 2}}}
          error-response {:error ["EQuery:Unknown asset"]}]
      (are [response result error asset]
           (with-fake-routes (json-handler (:asset-info krakenx/routes) response)
             (assert-asset-info-response (krakenx/get-asset-info {:asset asset} krakenx-host)
                                         result
                                         error))

           success-response (:result success-response) (:error success-response) ["ZEUR" "XETH"]
           error-response (:result error-response) (:error error-response) ["foo"])))

  (testing "default host"
    (let [response {:error []
                    :result {:XETH {:aclass "currency", :altname "ETH", :decimals 10, :display_decimals 5}
                             :ZEUR {:aclass "currency", :altname "EUR", :decimals 4, :display_decimals 2}}}]
      (with-fake-routes {(str krakenx/kraken-host (:asset-info krakenx/routes))
                         {:post (fn [request]
                                  (is(= "{\"asset\":\"ZEUR,XETH\"}"  (-> request :body slurp)))
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-asset-info-response (krakenx/get-asset-info {:asset ["ZEUR" "XETH"]}) (:result response) [])))))
