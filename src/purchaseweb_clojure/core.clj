(ns purchaseweb-clojure.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(defn read-category []
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                         (str/split line #","))
                    purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (zipmap header line))
                    purchases)]
    purchases))

(defn purchaseweb-html [category]
  (let [purchases (read-category)
        purchases (if (= 0 (count category))
                    purchases
                    (filter (fn [purchases]
                              (= (get purchases "category") category))
                      purchases))]
    [:ol
     (map (fn [purchases]
            [:li (str 
                   (get purchases "credit_card") " [" 
                   (get purchases "cvv") "] : " 
                   (get purchases "date"))])
       purchases)]))

(c/defroutes app
  (c/GET "/:category{.*}" [category]
    (h/html [:html
             [:body 
              [:a {:href "/"} "Full Listing"]
              " : "
              [:a {:href "/Alcohol"} "Alcohol"]
              " | "
              [:a {:href "/Food"} "Food"]
              " | "
              [:a {:href "/Furniture"} "Furniture"]
              " | "
              [:a {:href "/Jewelry"} "Jewelry"]
              " | "
              [:a {:href "/Shoes"} "Shoes"]
              " | "
              [:a {:href "/Toiletries"} "Toiletries"]
              (purchaseweb-html category)]])))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
