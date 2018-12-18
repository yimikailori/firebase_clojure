; if using luminus and compojure,
; initiate function should be in core.clj and its always initiated once
; else it will throw error
(defn initiate []
  (let [fileinput "path/to/firebase-adminsdk.json"
        googleserv (FileInputStream. fileinput)
        options (-> (FirebaseOptions$Builder.)
                    (.setCredentials (GoogleCredentials/fromStream googleserv))
                    (.setDatabaseUrl "https://firebaseio.com/") ; note firebase URL
                    (.build))
        _ (FirebaseApp/initializeApp options)]))
        
        
; below will pull records for a particular collection e.g test_feeds/
; if firebase path to collection is https://firebaseio.com/test_feeds/
; running (serviceacc) will print collection record
; then you can parse and manipulate the records to suite your taste
(defn serviceacc []
  (let [ref (-> (FirebaseDatabase/getInstance)
                (.getReference "test_feeds/")
                (.addListenerForSingleValueEvent (reify ValueEventListener
                                                   (onDataChange [this datasnap]
                                                      (println (.getValue datasnap))
                                                     ;(log/info "value=" (js/read-str (json/generate-string (.getValue datasnap)) :key-fn keyword))
                                                     ))))]))
                                                     
; below will insert a hashmap into a firebase collection
; assuming test_feeds has three records i.e content, image_url,uuid
(defn addingvalues [content image_url]
  (let [ref (-> (FirebaseDatabase/getInstance)
                (.getReference "test_feeds/"))    ; still using test_feeds/ collections
        feedref (.child ref "feedinfo")           ; child collection under test_feeds/ i.e https://firebaseio.com/test_feeds/feedinfo
        key (.getKey (.push feedref))             ; create a unique key automatically and push as child of collection
     
        values (assoc {} "content" content "image_url" image_url "uuid" key)]
    ;(log/infof "processing values into firebase:%s|%s" values key)
    (.setValueAsync  (.child feedref key) values)))   ; add values as children under the unique key
