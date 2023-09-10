(ns se.w3t.blueprint.portfolio
  (:require [portfolio.react-18 :refer-macros [defscene]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i a]]
            [com.fulcrologic.fulcro.application :as app-default]
            [se.w3t.blueprint.message :refer [ui-message]]
            ["react-dom/client" :as dom-client]
            [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
            [se.w3t.blueprint.portfolio.draggable :refer [ui-draggable-scene]]
            [se.w3t.blueprint.portfolio.sortable-list-scene :refer [ui-sortable-list-scene]]
            [se.w3t.blueprint.toggle :refer [ui-toggle]]
            [portfolio.ui :as ui]))

(defonce reactRoot (volatile! nil))

(defonce app
  (->
   (app-default/fulcro-app {:render-root! (fn [ui-root mount-node]
                                            (when-not @reactRoot
                                              (vreset! reactRoot (dom-client/createRoot mount-node)))
                                            (.render @reactRoot ui-root))})
   (with-react18)))

(comp/defsc Root [this {:root/keys [] :as props}]
  {:query [                      ;{:root/plane (comp/get-query Plane)}
                                        ;{:root/main (comp/get-query Main)}
           ]
   :initial-state (fn [_] { ;:root/plane (comp/get-initial-state Plane)
                           })
   :initLocalState (fn [_]
                     {:inverted? false})}
  (let [inverted? (comp/get-state this :inverted?)]
    (div {:className "root"
          :style {:background-color (if inverted? "#19181a" "#FFFFFF")
                  :width "100vw"
                  :height "100vh"}}
      (ui-sortable-list-scene))
    ))

(def ui-root (comp/computed-factory Root))

(comp/defsc Blah [this {:blah/keys [] :as props}]
  {:query [                      ;{:root/plane (comp/get-query Plane)}
                                        ;{:root/main (comp/get-query Main)}
           ]
   :initial-state (fn [_] { ;:root/plane (comp/get-initial-state Plane)
                           })
   :initLocalState (fn [_]
                     {:inverted? false})}
  (div {} "XXXXXXXXXXX"))

(def ui-blah (comp/computed-factory Blah))

(defscene first-scene (ui-message {:message/id 0
                                   :message/content "Hi, this is a short info message."
                                   :message/type :info
                                   }))

(defn init []
  (app-default/mount! app Root "app")
  #_(ui/start!
   {:config {:css-paths ["./public/css/main.css"]}}))

(defn refresh []
  (app-default/mount! app Root "app"))
