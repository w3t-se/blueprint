(ns se.w3t.codo.blueprint.editable-button
 (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
           #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div text form a i p img span button]]
              :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div text form a i p img span button]])
           [com.fulcrologic.fulcro.mutations :as m]
           [com.fulcrologic.fulcro.algorithms.tempid :as tempid]           
           [se.w3t.flowbite.factories :as f]))

;; #?(:cljs
;;    (m/defmutation change-a-query [_]
;;      (action [{:keys [state]}]
;;        (comp/set-query* state A {:query [:id :new-prop {:x (comp/get-query X state-map}]})))))

;; (comp/defsc EditableButton [this {:keys [id] :as props}]
;;   {:ident (fn [id] [:button/id id])
;;    :query (fn [] [:button/id
;;                   {:button/component []}])
;;    :initLocalState (fn [this props] {:edited? false})
;;    :initial-state (fn [{:keys [id title card] :as params}] {:button/id (or id (tempid/tempid))
;;                                                             :button/component []})
;;    :preserve-dynamic-query? true}
;;   (let [{:keys [on-click on-change submit-fn]} (comp/get-computed this)]
;;     (f/ui-button (if (not (tempid/tempid? id))
;;                    {:onClick on-click}
;;                    {})
;;       (if (and (tempid/tempid? id)
;;                (not (comp/get-state this :edited?)))
;;         (form {:onSubmit submit-fn}
;;           (f/ui-text-input {:onChange on-change #(m/set-string! this :requirement/title :event %)
;;                             :onBlur submit-fn}))
;;         (text :.ml-2 title)))))

(comp/defsc EditableButton [this props]
  {:initLocalState (fn [this props]
                     (println (comp/get-computed this))
                     {:edited? false})}
  (let [{:keys [on-click on-change submit-fn color classes]} (comp/get-computed this)
        button-map (-> {:classes (concat [""] classes)}
                       (#(if color (conj % {:color color})))
                       (#(if (not (tempid/tempid? (:id props))) (conj % {:onClick on-click}))))
        s-fn (fn [e]
               (.preventDefault e)
               (comp/set-state! this {:edited? true})
               (submit-fn))]
    (dom/button button-map
      (if (and (tempid/tempid? (:id props))
               (not (comp/get-state this :edited?)))
        (form {:onSubmit s-fn}
          (f/ui-text-input {:onChange on-change
                            :onBlur s-fn}))
        (dom/text {:class "truncate text-white font-lg font-bold"} (:title props))))))


(def ui-editable-button (comp/computed-factory EditableButton #_{:qualifyer :button/component}))
