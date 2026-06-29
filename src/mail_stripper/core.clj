(ns mail-stripper.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure-mail.parser :as parser]
            [clojure-mail.message :as message]
            [clojure-mail.core :as clojure-mail])
   (:import (javax.mail Session)
            (javax.mail.internet MimeMessage MimeBodyPart MimeMultipart MimeUtility)
            (java.io ByteArrayInputStream ByteArrayOutputStream)
            (java.util Properties)))


(defn encode-string
  "Encode a string using quoted-printable or base64"
  [s encoding]
  (let [baos (ByteArrayOutputStream.)
        out (MimeUtility/encode baos encoding)]
    (.write out (.getBytes s "UTF-8"))
    (.flush out)
    (String. (.toByteArray baos) "UTF-8")))


(defn string->message
  "Create a MimeMessage from a string"
  [email-str]
  (let [props (Session/getDefaultInstance (Properties.))]
    (MimeMessage. props (ByteArrayInputStream. (.getBytes email-str "UTF-8")))))


(defn clean-html
  "Your HTML cleaning function"
  [html]
  (-> html
      (str/replace #"<style[\n]*.*?>" "<govno hidden>")
      (str/replace #"</style>" "</govno>")
      (str/replace #"<img[\n]*.*?>" "<govno hidden>")
      (str/replace #"</img>" "</govno>")
      (str/replace #"color=\"[\n]*.*?\"" "govno=\"\"")
      (str/replace #"style=[\"\']([^\"\']*)[\"\']" "govno=\"\"")))


(defn recursively-parse-message
  [m]
  
  (let [content
        (try (.getContent m) (catch Exception e nil))
        
        count
        (try (.getCount content) (catch Exception e nil))
        
        bodies
        (try
          (map (fn [i] (.getBodyPart content i)) 
            (range count))
          (catch Exception e nil))]
    
    (if (some? bodies)
      (map recursively-parse-message bodies)
      (map (fn [x]
               {:part x
                :type (.getContentType x)
                :encoding (.getEncoding x)
                :content (.getContent x)
                :raw (slurp (.getRawInputStream x))})
        [m]))))


(defn -main
  [s]
  (let
    [{:keys [content encoding raw] :as html-part}
     (->> s
      string->message
      recursively-parse-message
      flatten
      (filter (fn [x] (re-find #"text/html" (:type x))))
      first)
     
     fixed-html
     (if (some? content)
       (encode-string (clean-html content) encoding))]
    
    (if (some? fixed-html)
      (print (str/replace s raw fixed-html))
      (print s))))



(comment
  
  (-main (slurp "fixtures/ofd.eml"))
  )





