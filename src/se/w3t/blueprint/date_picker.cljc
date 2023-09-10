(ns se.w3t.blueprint.date-picker
  (:require [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.react.hooks :refer [use-state]]
                #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
                   :cljs [com.fulcrologic.fulcro.dom :as dom])
                #?(:cljs ["tailwind-datepicker-react" :default Datepicker])))

#?(:cljs
   (def ui-datepicker (interop/react-factory Datepicker))
   :clj (def ui-datepicker nil))

(comp/defsc DateInput [this props]
  {:use-hooks? true}
  (let [[show setShow] (use-state false)]
    (dom/div {}
      (dom/button {:class "" :onClick #(setShow true)} (str "s: " show))
      (ui-datepicker {:options {:todayBtn true :clearBtn true :language "en"
                                
                                :theme {}
                                :autoHide true}
                      :show show
                                        ;:value 
                      :setShow setShow #_(fn [s]
                                           (println "show: "(comp/get-state this :show?))
                                           (println "state1: " s)
                                           (setShow false)
                                           (println "state: " show)
                                        ;(println "state: " ((second st) s))
                                           #_(j/call (second st) "dispatchSetState" true)
                                        ;(println "state: " st)
                                           (comp/set-state! this {:show? s}))
                      :onChange (fn [e]
                                  (println e)
                                  #_((:onChange props) e))}))))

(def ui-date-input (comp/computed-factory DateInput))
