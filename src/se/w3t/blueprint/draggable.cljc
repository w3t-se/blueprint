(ns se.w3t.blueprint.draggable
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div i a p h1 section tr td table button head]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i a p h1 section tr td table button head]])

            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            
            #?(:cljs [goog.object :as gobj])
            ;;#?(:cljs ["mouse-speed" :as mp :refer [MouseSpeed]])

            [clojure.core.async :as as :refer [go <! >! go-loop]]
                                        ;#?(:cljs ["react-motion" :refer [Motion spring]])
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))

;; (def ui-motion (interop/react-factory Motion))

;; (def ui-mouse-speed (interop/react-factory MouseSpeed))

;; (defn on-calc-speed [speed]
;;   (js/console.log speed))

;; (defn- spring-constant [x]
;;   (-> (- x (-> (* 3 10)
;;              (- 50)
;;              (/ 2)))
;;     (/ 15)))

(defn event->dom-coords
  "Translate a javascript evt to a clj [x y] within the given dom element."
  [evt dom-ele & {:keys [bounding-rect]}]
  (let [cx (.-clientX evt)
        cy (.-clientY evt)
        ;;parentOffsetX (-> evt .-target .-offsetLeft)
        ;;parentOffsetY (-> evt .-target .-offsetTop)
        BB (if bounding-rect (.getBoundingClientRect dom-ele))
        x  (- (+ (- cx (if bounding-rect (.-left BB) 0)) (.-scrollLeft dom-ele)) 0)
        y  (+ (- cy (if bounding-rect (.-top BB) 0) (.-scrollTop dom-ele)) 0)]
    [x y]))

(defn vector-minus [a b]
  [(- (nth a 0) (nth b 0))
   (- (nth a 1) (nth b 1))])

(defn vector-plus [a b]
  [(+ (nth a 0) (nth b 0))
   (+ (nth a 1) (nth b 1))])

(defn clip-to-bounding-rect [pos bounding-rect]
  (let [x (first pos)
        y (second pos)
        left (nth bounding-rect 0)
                                        ;right (nth bounding-rect 1)
        top (nth bounding-rect 1)
                                        ;bottom (nth bounding-rect 4)
        ]
    (if bounding-rect
      [(if (< x left) left x) (if (> y top) top y)]
      pos)))

#_(defmacro DraggableFactory [n child-query ui-child]
  `(comp/defsc ~(symbol (str n)) ~'[this {:draggable/keys [id pos child container-id] :as props}]
    {:ident ~'(fn [] [:draggable/id id])
     :query [:draggable/id
             :draggable/pos
             {:draggable/child ~child-query}
             :draggable/container-id]
     :initial-state ~'(fn [{:keys [id pos child container-id] :as params}]
                      {:draggable/id (or id (tempid/tempid))
                       :draggable/child (or child nil)
                       :draggable/pos (or pos [0 0])
                       :draggable/container-id (or container-id "plane")})
     :initLocalState ~'(fn []
                       {:pos nil})}
    (let [~'plane (comp/get-computed ~'this :plane)
          ~'pos (or (comp/get-state ~'this :pos) ~'pos)]
      (dom/div {:style {:fill-opacity "1"
                        :width "40px"
                        :height "40px"
                        :margin 0
                        :position "absolute"
                        :cursor "grab"
                        :transform (str "translate(" (first ~'pos) "px," (second ~'pos) "px)")}
                :ref ~'(fn [r]
                       (when [r]
                         #?(:cljs (gobj/set this (str id) r))))
                 
                :onMouseDown (fn [~'e]
                               (let [~'plane (comp/get-computed ~'this :plane)
                                     ~'displacement #?(:cljs (event->dom-coords ~'e #?(:cljs (gobj/get ~'this (str ~'id)))))]
                                 (comp/update-state! ~'plane assoc-in :move-fn (fn [~'evt]                                           
                                                                                 (let [~'p (draggable/vector-minus (draggable/event->dom-coords ~'evt #?(:cljs (gobj/get ~'plane ~'container-id)))
                                                                                                  ~'displacement)]
                                                                              (comp/update-state! ~'this assoc-in :pos ~'p))))))
                :onMouseUp (fn [~'e]
                             (when-let [~'pos (comp/get-state ~'this :pos)]
                               (comp/update-state! ~'this assoc {:pos nil})))}
               (~ui-child ~'child (comp/get-computed ~'this))))))

(defn draggable-div [this id pos plane container-id children & {:keys [draggable? bounding-rect extra-classes]}]
  (dom/div {:class "flex"
            :style {:position "absolute"
                    :transform (str "translate(" (first pos) "px," (second pos) "px)")}
            :classes (into [] extra-classes)
            :ref (fn [r]
                   (when [r]
                     #?(:cljs (gobj/set this (str id) r))))
            :onClick (fn [e] (.preventDefault e))
            :onMouseDown (fn [e]
                           #_(.stopPropagation e)
                           (if draggable?
                             (let [displacement (event->dom-coords e #?(:cljs (gobj/get this (str id))))]
                               (comp/update-state! plane assoc :move-fn (fn [evt]
                                                                          (let [p (clip-to-bounding-rect (vector-minus (event->dom-coords evt #?(:cljs (gobj/get plane container-id)))
                                                                                                                       displacement)
                                                                                                         bounding-rect)]
                                                                            (comp/update-state! this assoc :pos p))))
                               (comp/update-state! plane assoc :mouse-up-fn (fn [evt]
                                                                              (comp/update-state! this assoc :pos nil)
                                                                              (comp/update-state! plane assoc :move-fn nil)
                                                                              )))))
             :onMouseUp (fn [e]
                          #_(.stopPropagation e)
                          (when-let [pos (comp/get-state this :pos)]
                            (comp/update-state! plane assoc :move-fn nil)
                            ))}
            children))

#_(defmacro draggable-div [pos plane container-id children & {:keys [draggable? bounding-rect]}]
  `(dom/div {:style {:position "absolute"
                     :transform (str "translate(" (first ~pos) "px," (second ~pos) "px)")}
             :ref ~'(fn [r]
                      (when [r]
                        #?(:cljs (gobj/set this (str id) r))))
             
             :onMouseDown (fn [~'e]
                            (if [~draggable?]
                              (let [~'displacement (draggable/event->dom-coords ~'e #?(:cljs (gobj/get ~'this (str ~'id))))]
                                (comp/update-state! ~plane assoc :move-fn (fn [~'evt]
                                                                            (let [~'p (clip-to-bounding-rect (draggable/vector-minus (draggable/event->dom-coords ~'evt #?(:cljs (gobj/get ~plane ~container-id)))
                                                                                                                                     ~'displacement)
                                                                                                             ~bounding-rect)]
                                                                              (comp/update-state! ~'this assoc :pos ~'p)
                                                                              )))
                                (comp/update-state! ~plane assoc :mouse-up-fn (fn [~'evt]
                                                                                (comp/update-state! ~'this assoc :pos nil)
                                                                                )))))
             :onMouseUp (fn [~'e]
                          (when-let [~'pos (comp/get-state ~'this :pos)]
                            (comp/update-state! ~'this assoc :pos nil)))}
     ~children))

                                        ;(def ui-draggable (comp/computed-factory Draggable {:keyfn :draggable/id}))


                                        ;(DraggableFactory "DraggableTrain" (comp/get-query ActionTrain)  ui-action-train)


(defn ref-fn-factory [this id]
  (fn [r]
    (when [r]
      #?(:cljs (gobj/set this (str id) r)))))


(defsc DraggableArea [this props]
  {:ident (fn [] [::DraggableArea (:id props)])
   :initLocalState (fn [this props]
                     {:ref-fn (ref-fn-factory this (:id props))
                      :on-mouse-move nil
                      :on-mouse-up nil
                      :item-dragging #?(:clj nil
                                        :cljs (atom nil) #_(as/chan nil))})}
  (let [{:keys [ref-fn on-mouse-move on-mouse-up]} (comp/get-state this)]
    (div {:ref ref-fn
          :classes (:classes props)
          :style (merge {} (:style props))
          :onMouseMove on-mouse-move
          :onMouseUp on-mouse-up}
      (comp/children this))))

(def ui-draggable-area (comp/factory DraggableArea))

(defn on-mouse-up-factory [this props]
  (fn [e]
    (let [plane (last (comp/class->all this DraggableArea))
          item-dragging-chan (comp/get-state plane :item-dragging)]
      #_(reset! item-dragging-chan nil)
      #_(println "dsa")
      #_(go (as/into [] item-dragging-chan))
      (comp/set-state! this {:dragging? false
                             :end-pos (comp/get-state this :pos)})
      (comp/set-state! plane {:on-mouse-move nil
                              :item-dragging (atom nil)
                              :on-mouse-up nil}))))

(defn on-mouse-down-factory [this props]
  (let [{:keys [id plane container-id draggable? bounding-rect keep-position?]} props
        plane (last (comp/class->all this DraggableArea))
        plane-obj #?(:clj nil
                     :cljs (gobj/get plane container-id))
        this-obj #?(:clj nil
                    :cljs (gobj/get this id))
        move-fn-factory (fn [displacement this props]
                          (fn [evt]
                            (let [plane (last (comp/class->all this DraggableArea))
                                  plane-obj #?(:clj nil
                                               :cljs (gobj/get plane container-id))
                                  end-pos (if keep-position? (comp/get-state this :end-pos))
                                  p (clip-to-bounding-rect (vector-minus (event->dom-coords evt plane-obj)
                                                                         displacement)
                                                           bounding-rect)
                                  p (if end-pos (vector-plus p end-pos) p)]
                              (comp/set-state! plane {:was-dragged? true})
                              (comp/set-state! this {:pos p}))))]
    (fn [e]
      (let [plane (last (comp/class->all this DraggableArea))
            plane-obj #?(:clj nil
                         :cljs (gobj/get plane container-id))
            item-dragging-chan (comp/get-state plane :item-dragging)
            this-obj #?(:clj nil
                        :cljs (gobj/get this id))
            displacement (event->dom-coords e this-obj)
            end-pos (if keep-position? (comp/get-state this :end-pos))]
        #?(:cljs
           (reset! item-dragging-chan {:id (:id props)
                                       :height (.-clientHeight this-obj)
                                       :width (.-clientWidth this-obj)}))
        (comp/set-state! this {:dragging? true
                               :pos (if end-pos end-pos (clip-to-bounding-rect (vector-minus (event->dom-coords e plane-obj)
                                                                                             displacement)
                                                                               bounding-rect))
                               :height (.-clientHeight this-obj)
                               :width (.-clientWidth this-obj)})
        (comp/set-state! plane {:on-mouse-move (move-fn-factory displacement this props)
                                :was-dragged? false
                                :on-mouse-up (on-mouse-up-factory this props)})))))

(defsc Draggable [this props]
  {:ident (fn [] [::Draggable (:id props)])
   :initLocalState (fn [this props]
                     (let [{:keys [id init-pos]} props]
                       {:pos (if init-pos init-pos [0 0])
                        :dragging? false
                        :ref-fn (ref-fn-factory this id)
                        :on-mouse-down (on-mouse-down-factory this props)}))
   :componentDidMount (fn [this] (let [props (comp/props this)
                                       {:keys [drag-handle-id parent id]} props
                                       drag-handle #?(:clj nil
                                                      :cljs (gobj/get parent drag-handle-id))
                                       this-handle #?(:clj nil
                                                      :cljs (gobj/get this id))
                                       on-mouse-down (on-mouse-down-factory this props)]
                                   (if drag-handle
                                     (.addEventListener drag-handle "mousedown" on-mouse-down))
                                   (.addEventListener this-handle "click"
                                                      (fn [e] (let [drag (last (comp/class->all this DraggableArea))
                                                                    was-dragged? (comp/get-state drag :was-dragged?)]
                                                                (if was-dragged?
                                                                  (.stopPropagation e)
                                                                  (comp/set-state! drag {:was-dragged? false})))) true)
                                   (comp/set-state! this {:on-mouse-down on-mouse-down
                                                          :drag-handle drag-handle})))}
  (let [{:keys [classes draggable? style keep-position? on-mouse-up]} props
        {:keys [ref-fn on-mouse-down dragging? pos height width drag-handle was-dragged?]} (comp/get-state this)]
    (dom/div (merge {:style (merge {}
                                   style
                                   (if dragging?
                                     {:height height
                                      :width width
                                      :transform (str "translate(" (first pos)  "px," (second pos) "px)")}
                                     (if keep-position?
                                       {:transform (str "translate(" (first pos)  "px," (second pos) "px)")})))
                     :draggable? (or draggable? true)
                     :classes (into [(if dragging? "absolute z-50 w-fit h-fit")] classes)
                     :ref ref-fn}
                    (if drag-handle {} {:onMouseDown on-mouse-down})
                    #_(if on-mouse-up {:onMouseUp on-mouse-up}))
      (comp/children this))))

(def ui-draggable (comp/factory Draggable {:keyfn (fn [props] (str ::Draggable (:id props)))}))
