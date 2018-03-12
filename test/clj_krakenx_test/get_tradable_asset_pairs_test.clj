(ns clj-krakenx-test.get-tradable-asset-pairs-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [cheshire.core :as cheshire]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-tradable-asset-pairs-response
  [response result error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (-> response :krakenx/unmarshal-error nil?)))


(deftest get-tradable-asset-pairs-test
  (testing "valid response"
    (let [success-response {:error []
                            :result {:XETHZEUR {:lot_decimals 8, :altname "ETHEUR"}}}
          error-response {:error ["EGeneral:Unknown error"]}
          all-response {:error [], :result {:XETHZEUR {:lot_decimals 8, :altname "ETHEUR"}
                                            :EOSETH {:lot_decimals 8, :altname "EOSETH"}}}]
      (are [response result error opts]
           (with-fake-routes (json-handler (:tradable-asset-pairs krakenx/routes) response)
             (assert-tradable-asset-pairs-response (krakenx/get-tradable-asset-pairs opts krakenx-host)
                                         result
                                         error))

           success-response (:result success-response) (:error success-response) {:pair ["XETHZEUR" "EOSETH"]}
           error-response (:result error-response) (:error error-response) {:pair ["foo"]}
           all-response (:result all-response) (:error all-response) nil)))

  (testing "default host, filter pairs"
    (let [response {:error [], :result {:XETHZEUR {:lot_decimals 8, :altname "ETHEUR"}
                                        :EOSETH {:lot_decimals 8, :altname "EOSETH"}}}]
      (with-fake-routes {(str krakenx/kraken-host (:tradable-asset-pairs krakenx/routes))
                         {:post (fn [request]
                                  (is(= "{\"pair\":\"XETHZEUR,EOSETH\"}"  (-> request :body slurp)))
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-tradable-asset-pairs-response (krakenx/get-tradable-asset-pairs {:pair ["XETHZEUR" "EOSETH"]})
                                              (:result response)
                                              (:error response)))))

  (testing "default host, get them all"
    (let [response {:error [], :result {:XETHZEUR {:lot_decimals 8, :altname "ETHEUR"}
                                        :EOSETH {:lot_decimals 8, :altname "EOSETH"}}}]
      (with-fake-routes {(str krakenx/kraken-host (:tradable-asset-pairs krakenx/routes))
                         {:post (fn [_]
                                  {:status 200, :body (cheshire/generate-string response)})}}
        (assert-tradable-asset-pairs-response (krakenx/get-tradable-asset-pairs)
                                              (:result response)
                                              (:error response))))))
