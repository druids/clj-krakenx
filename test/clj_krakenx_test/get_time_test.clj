(ns clj-krakenx-test.get-time-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-krakenx.core :as krakenx]
    [clj-krakenx-test.fake-http :as fake-http]))


(def krakenx-host "https://api.kraken.localhost")

(def get-time (partial krakenx/get-time krakenx-host))

(def json-handler (partial fake-http/json-handler krakenx-host))


(defn- assert-time-response
  [response result error unmarshal-error]
  (is (= result (:krakenx/result response)))
  (is (= error (:krakenx/error response)))
  (is (= unmarshal-error (:krakenx/unmarshal-error response))))


(deftest get-time-test
  (testing "valid response"
    (let [time-response {:error [], :result {:unixtime 1520598777, :rfc1123 "Fri,  9 Mar 18 12:32:57 +0000"}}
          error-response {:error ["EGeneral:Invalid arguments"]}]
      (are [response result error unmarshal-error]
           (with-fake-routes (json-handler (:time krakenx/routes) response)
             (assert-time-response (krakenx/get-time krakenx-host) result error unmarshal-error))

           time-response (:result time-response) (:error time-response) nil
           error-response (:result error-response) (:error error-response) nil)))

  (testing "invalid response"
    (let [invalid-response "{asdf"
          error-message (str "Unexpected character ('a' (code 97)): was expecting double-quote to start field name\n at"
                             " [Source: (StringReader); line: 1, column: 3]")]
      (with-fake-routes {(str krakenx-host (:time krakenx/routes))
                         {:post (fn [_] {:status 200, :body invalid-response})}}
        (assert-time-response (krakenx/get-time krakenx-host) nil nil error-message))))

  (testing "default host"
    (let [response {:error [], :result {:unixtime 1520598777, :rfc1123 "Fri,  9 Mar 18 12:32:57 +0000"}}]
      (with-fake-routes (fake-http/json-handler krakenx/kraken-host (:time krakenx/routes) response)
        (assert-time-response (krakenx/get-time) (:result response) [] nil)))))
