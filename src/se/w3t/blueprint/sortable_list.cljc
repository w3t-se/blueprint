(ns se.w3t.blueprint.sortable-list
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div i a p h1 section tr td table button head]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i a p h1 section tr td table button head]])
            [se.w3t.blueprint.draggable :as draggable :refer [ui-draggable DraggableArea]]
            [clojure.core.async :as as :refer [go <! >! go-loop]]
            #?(:cljs [goog.object :as gobj])
            #?(:clj [clojure.math :refer [ceil floor round]])))

(defn rotate [n s]
  (let [[front back] (split-at (mod n (count s)) s)]
    (concat back front)))

(defn get-order-from-pos [pos item-size number-items]
  (let [last-index-x (- (first number-items) 1)
        last-index-y (- (second number-items) 1)
        math-floor #?(:clj floor
                      :cljs js/Math.floor)
        order-x (math-floor (/ (first pos) (first item-size)))
        order-y (math-floor (/ (second pos) (second item-size)))
        res
        [(-> order-x
             (#(if (< % 0) 0 %))
             (#(if (> % last-index-x) last-index-x %)))
         (-> order-y
             (#(if (< % 0) 0 %))
             (#(if (> % last-index-y) last-index-y %))) ]]
    res))

(defn reorder-elements [order-list item-dragging order order-direction]
  (let [delta (if (= order-direction :horizontal)
                (first (draggable/vector-minus order item-dragging))
                (second (draggable/vector-minus order item-dragging)))
        order (if (= order-direction :horizontal) (first order) (second order))
        item-dragging (if (= order-direction :horizontal) (first item-dragging) (second item-dragging))]
    (cond (= delta 0) order-list
          (> delta 0)
          (vec (concat (subvec order-list 0 item-dragging)
                       (vec (rotate 1 (subvec order-list item-dragging (+ order 1))))
                       (subvec order-list (+ order 1))))
          (< delta 0)
          (vec (concat (subvec order-list 0 order)
                       (vec (rotate -1 (subvec order-list order (+ item-dragging 1))))
                       (subvec order-list (+ item-dragging 1))))
          :default order-list)))

(defn get-id-from-dragged [dragged]
  (let [id (:id dragged)]
    (int (last (clojure.string/split id #"_")))))

(defn on-mouse-down-factory [this props]
  (let [{:keys [id order-direction]} props
        plane (last (comp/class->all (comp/any->app this) draggable/DraggableArea))
        this-obj #?(:clj nil
                    :cljs (gobj/get this id))
        move-fn-factory (fn [e]
                          (let [{:keys [item-height item-width item-dragging order-list nr-items]} (comp/get-state this)
                                this-obj #?(:clj nil
                                            :cljs (gobj/get this id))
                                p (last (comp/class->all (comp/any->app this) draggable/DraggableArea))
                                pos (draggable/event->dom-coords e this-obj {:bounding-rect true})
                                order (get-order-from-pos pos [item-width item-height] nr-items)]
                            (if-not (comp/get-state p :was-dragged?) (comp/set-state! p {:was-dragged? true}))
                            (comp/set-state! this {:order order})))
        mouse-up-factory (fn [e]
                           (let [{:keys [order order-list item-dragging]} (comp/get-state this)
                                 ;plane (last (comp/class->all (comp/any->app this) draggable/DraggableArea))
                                 ;; item-dragging-chan (comp/get-state plane :item-dragging)

                                 ;order-vec (if (= order-direction :horizontal) [c 1] [1 c])
                                 ;pos (draggable/event->dom-coords e this-obj {:bounding-rect true})
                                 ;order (get-order-from-pos pos [item-width item-height] order-vec)
                                 ]
                             #_(println (reorder-elements order-list item-dragging order))
                             #_(comp/set-state! this {:order-list (reorder-elements order-list item-dragging order)})
                             #_(go (as/into [] item-dragging-chan))
                             (comp/set-state! this {:order-list (reorder-elements order-list item-dragging order order-direction)
                                                    :item-dragging nil
                                                    :on-mouse-move nil
                                                    :on-mouse-up nil})))]
    (fn [e]
      (let [{:keys [id order-direction]} props
            {:keys [item-height item-width item-dragging order-list]} (comp/get-state this)
            plane (last (comp/class->all (comp/any->app this) draggable/DraggableArea))
            this-obj #?(:clj nil
                        :cljs (gobj/get this id))
            c (count order-list)
            order-vec (if (= order-direction :horizontal) [c 1] [1 c])
            dragging-chan (comp/get-state plane :item-dragging)
            item @dragging-chan
            #_(let [i (<! dragging-chan)]
                (println "get dragging chan: " (str i))
                i)
            order (get-order-from-pos (draggable/event->dom-coords e this-obj {:bounding-rect true})
                                      [(:width item) (:height item)]
                                      order-vec)
            ;;item-dragging (get-id-from-dragged item)
            item-dragging order #_(condp = order-direction
                                    :horizontal (first order)
                                    :grid (+ (first order) (* (second order) 8))
                                    (second order))
            ]
        (comp/set-state! this
                         {:item-height (:height item)
                          :item-width (:width item)
                          :pos (draggable/event->dom-coords e this-obj {:bounding-rect true})
                          :top-offset (-> e .-target .-offsetTop)
                          :item-dragging item-dragging
                          :order order
                          :on-mouse-move move-fn-factory
                          :on-mouse-up mouse-up-factory})))))

(defn swap [v i1 i2] 
  (assoc v i2 (v i1) i1 (v i2)))

(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn get-item-id [item]
  (str (let [n (-> item keys first namespace)
             id (get item (keyword n "id"))] id)))

#_(defmutation swap-items [{:keys [item-list ident & send-remote]}]
   (action [{:keys [app state]}]
     ))

(defn parse-break-points-cols [this id classes]
  (let [this-obj #?(:clj nil
                    :cljs (gobj/get this id))
        children (first (comp/children this))
        width (.clientWidth this-obj)
        height (.clientWidth this-obj)]
    ))

(defsc SortableList [this props]
  {:ident (fn [] [::SortableList (:id props)])
   :initLocalState (fn [this props]
                     (let [{:keys [id items item-comp order-direction]} props
                           order-list (vec (range (count (comp/children this))))]
                       {:id id
                        :order-list order-list
                        :nr-items (if (= order-direction :horizontal) [(count order-list) 1] [1 (count order-list)])
                        :ref-fn (draggable/ref-fn-factory this id)
                        :item-dragging nil}))
   :componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (comp/set-state! this {:on-mouse-down (on-mouse-down-factory this props)
                                                 :order-list (vec (range (count (comp/children this))))})))}
  (let [{:keys [item-dragging ref-fn on-mouse-move on-mouse-down on-mouse-up
                item-height item-width order order-list element-removed?]} (comp/get-state this)
        {:keys [id container-id classes order-direction gap elements-removable? on-mouse-enter on-mouse-leave]} props
        children (comp/children this)
        cols 1
        gap (or gap 0)
        math-floor #?(:clj floor
                      :cljs js/Math.floor)
        rows (math-floor (/ (count order-list) cols))
        item-nr (if (= order-direction :horizontal) (first item-dragging) (second item-dragging)) #_(if item-dragging (+ (first item-dragging) (* cols (second item-dragging))))
        item-nr (if element-removed? (- item-nr 1) item-nr)
        order-nr (if order (+ (first order) (* cols (second order))))]
    (when (> (count children) 0)
      (dom/div {:classes (into ["relative"] classes)
                :ref ref-fn
                :onMouseUp on-mouse-up}
        (for [i (range (count order-list))]
          (dom/div (merge {:classes (conj ["relative"] (if item-dragging "transition-all duration-300"))
                          :style (merge {}
                                        (if (= i item-nr)
                                          {:top (* (if (= order-direction :horizontal) 0 -1) 1 item-height)
                                           :left (* -1 (+ -1 1) item-width)})
                                        (if (and item-dragging (not (= i item-nr)))
                                          (let [dx (- (first order) (first item-dragging))
                                                dy (- (second order) (second item-dragging))]
                                            (condp = order-direction
                                              :grid (let [tr-x (cond
                                                                 (and (> dx 0) (<= i (first order)) (> i item-nr)) (* -1 (+ item-width gap))
                                                                 (and (< dx 0) (>= i (first order)) (< i item-nr)) (* 1 (+ item-width gap)))
                                                          tr-y (cond
                                                                 (and (> dy 0) (<= i (second order)) (> i item-nr)) (* -1 (+ item-height gap))
                                                                 (and (< dy 0) (>= i (second order)) (< i item-nr)) (* 1 (+ item-height gap)))]
                                                      {:transform (str "translate(" tr-x "px," tr-y "px)")})
                                              :horizontal (cond
                                                            (and (> dx 0) (<= i (first order)) (> i item-nr)) {:transform (str "translate(" (* -1 (+ item-width gap)) "px," 0 "px)")}
                                                            (and (< dx 0) (>= i (first order)) (< i item-nr)) {:transform (str "translate(" (* 1 (+ item-width gap)) "px," 0 "px)")})
                                              (cond
                                                (and (> dy 0) (<= i (second order)) (> i item-nr)) {:transform (str "translate(" 0 "px," (* -1 (+ item-height gap)) "px)")}
                                                (and (< dy 0) (>= i (second order)) (< i item-nr)) {:transform (str "translate(" 0 "px," (* 1 (+ item-height gap)) "px)")})))))
                          :onMouseDown on-mouse-down
                          :onMouseMove on-mouse-move}
                          (if elements-removable?
                            {:onMouseEnter (fn [e]
                                             (let [plane (last (comp/class->all this DraggableArea))
                                                   item-dragging (comp/get-state plane :item-dragging)]
                                               (println "enter")
                                               (comp/set-state! this {:element-removed? false
                                                                      
                                                                      :nr-items (if (= order-direction :horizontal) [(count order-list) 1] [1 (count order-list)])}))
                                             #_(on-mouse-enter)
                                             )
                             :onMouseLeave (fn [e]
                                             (println "leave")
                                             (let [nr-items (if (= order-direction :horizontal) [(- (count order-list) 1) 1] [1 (- (count order-list) 1)])
                                                   plane (last (comp/class->all this DraggableArea))
                                                   item @(comp/get-state plane :item-dragging)
                                                   this-obj #?(:clj nil
                                                               :cljs (gobj/get this id))]
                                               (comp/set-state! this {:nr-items nr-items
                                                                      :element-removed? true
                                                                      :order (get-order-from-pos (draggable/event->dom-coords e this-obj {:bounding-rect true})
                                                                                                 [(:width item) (:height item)]
                                                                                                 nr-items)
                                                                      }))
                                             #_(on-mouse-leave))}))
            (if (and (not (= order-direction :horizontal)) (= item-nr i) (not (comp/get-state this :element-removed?)))
              (div {:style {:position "relative"
                            :height (comp/get-state this :item-height)
                            :width (comp/get-state this :item-width)}}))
            (ui-draggable {:id (str id "-" (nth order-list i))
                           :container-id container-id
                           :classes []}
                          (nth children (nth order-list i)))))))))

(def ui-sortable-list (comp/factory SortableList))
