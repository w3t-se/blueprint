(ns se.w3t.blueprint.portfolio.draggable
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [se.w3t.blueprint.draggable :refer [ui-draggable-area ui-draggable]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div img span form input textarea ul li
                                                        label h3 i a p h1 h2 h6 section tr td table button head]]))

(comp/defsc DraggableScene [this {:root/keys [] :as props}]
  {:query []
   :initial-state (fn [_] {})
   :initLocalState (fn [_]
                     {:inverted? false})}
  (let [inverted? (comp/get-state this :inverted?)]
    (ui-draggable-area {:id (str "req-view" "-draggable-area")
                        :classes ["w-full h-full relative"]}
                       (ui-draggable {:id (str "draggable-test")
                                      :classes ["absolute"]
                                      :keep-position? true
                                      :parent this
                                      :drag-handle-id (str "draggable-test" "-drag-handle")
                                      :container-id "req-view-draggable-area"}
                                     (dom/button {} "Drag me")))))

(def ui-draggable-scene (comp/computed-factory DraggableScene))
