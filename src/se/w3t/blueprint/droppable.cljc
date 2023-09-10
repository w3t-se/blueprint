(ns se.w3t.blueprint.droppable
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div i a p h1 section tr td table button head]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i a p h1 section tr td table button head]])
            
            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            
            #?(:cljs [goog.object :as gobj])
            #?(:cljs ["mouse-speed" :as mp :refer [MouseSpeed]])
                                        ;#?(:cljs ["react-motion" :refer [Motion spring]])
            [se.w3t.codo.components.helpers.draggable :as d]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))

(defsc Droppable [this props]
  {:initLocalState (fn [this props]
                     (let [{:keys [id plane container-id children draggable? bounding-rect extra-classes]} props
                           {:keys [plat]} this]
                       #_(println "plane:" plane)
                       {:pos [0 0]
                        :dragging? false
                        :ref-fn (d/ref-fn-factory this id)
                        :on-mouse-up (d/on-mouse-up-factory this props)
                        :on-mouse-down (d/on-mouse-down-factory this props)}))}
  (let [{:keys [id plane container-id children classes draggable? style placeholder]} props
        {:keys [ref-fn on-mouse-down on-mouse-up pos dragging?]} (comp/get-state this)
        ;;placeholder-size [(.-width (gobj/get this id)) (.-height (gobj/get this id))]
        ]
    (dom/div {;:style (merge {} style (if dragging? {:transform (str "translate(" (first pos)  "px," (second pos) "px)")}))
              ;:classes (into [(when dragging? "absolute")] classes)
              :ref ref-fn
              :onDrop (fn [e] (println "dropped"))
              :onDragOver (fn [e] (println "drag over"))}
      children
      )))

(def ui-droppable (comp/computed-factory Droppable))
