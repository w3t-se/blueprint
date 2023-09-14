(ns se.w3t.blueprint.portfolio.sortable-list-scene
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [se.w3t.blueprint.draggable :refer [ui-draggable-area DraggableArea ui-draggable]]
            [se.w3t.blueprint.sortable-list :refer [ui-sortable-list]]
            [se.w3t.flowbite.factories :as f]
            [com.fulcrologic.fulcro.dom :as dom :refer [div img span form input textarea ul li
                                                        label h3 i a p h1 h2 h6 section tr td table button head]]))

(comp/defsc SortableListScene [this props]
  {}
  (dom/div
      #_(div {} (str (comp/get-state (last (comp/class->all this DraggableArea)) :item-dragging)))
      (ui-draggable-area {:id (str "req-view" "-draggable-area")
                          :classes ["w-full h-full relative"]}
                         (ui-sortable-list {:id (str "sortable-list")
                                            :on-drop #(comp/transact! this `[])
                                            :classes ["w-full h-full grid 2xl:grid-cols-8 xl:grid-cols-6 auto-cols-max gap-2
                                                       border-gray-300 dark:border-gray-700 flex items-top"]
                                            :container-id "req-view-draggable-area"
                                            :order-direction :horizontal}
                                           (for [s [0 1 2 3 4]]
                                             (dom/button {:class "bg-black w-full text-white"
                                                          :style {:width "100px"}
                                                          :onClick (fn [e]
                                                                     (let [drag (last (comp/class->all this DraggableArea))
                                                                           was-dragged? (comp/get-state drag :was-dragged?)]
                                                                       (if-not was-dragged?
                                                                         (println "click"))))}
                                               (str "Sortable: " s)))))))

(def ui-sortable-list-scene (comp/computed-factory SortableListScene))
