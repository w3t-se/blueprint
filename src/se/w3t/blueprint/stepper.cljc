(ns se.w3t.blueprint.stepper
 (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
           #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div h3 a i p li ol img span button]]
              :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div a li ol h3 i p img span button]])

           [com.fulcrologic.fulcro.dom.events :as events]
           [se.w3t.blueprint.icons.solid :as s :refer [check clipboard-document-list cube]]
           [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
           [se.w3t.flowbite.factories :as f]))

(def icon-map {:check check
               :clipboard-document-list clipboard-document-list
               :cube cube})

#_(def ui-icon (comp/computed-factory Icon))

(comp/defsc Step [this {:step/keys [id heading details icon completed? active?] :as props}]
  {:ident :step/id
   :initial-state (fn [{:keys [id heading details icon completed? active?] :as params}]
                    {:step/id (or id (tempid/uuid))
                     :step/heading heading :step/details details
                     :step/icon icon :step/completed? completed?
                     :step/active? active?})
   :query [:step/id :step/heading :step/details
           :step/icon :step/completed? :step/active? :step/onClick]}
  (let [onClick (comp/get-computed this :onClick)]
    (li {:class "mb-10 ml-6"}
      (if completed?
        (span {:class "absolute flex items-center justify-center w-8 h-8
                       bg-green-200 rounded-full -left-4 ring-4 ring-white
                       dark:bg-green-900"}
          (div {:class "w-5 h-5 text-green-500 dark:text-green-400"}
            check))
        (span {:class "absolute flex items-center justify-center w-8 h-8
                       bg-gray-100 rounded-full -left-4 ring-4 ring-white
                       dark:ring-zinc-900 dark:bg-zinc-700"}
          (div {:class (if active?
                         "w-5 h-5 text-black dark:text-white"
                         "w-5 h-5 text-gray-500 dark:text-gray-400")}
            (icon icon-map))))
      (div {:class "hover:cursor-pointer"
            :onClick onClick}
        (h3 {:class (if active?
                      "font-medium leading-tight text-black dark:text-white"
                      "font-medium leading-tight")} heading)
        (p {:class (if active?
                     "text-sm text-black dark:text-white"
                     "text-sm")} details)))))

(def ui-step (comp/computed-factory Step {:keyfn :step/id}))

(comp/defsc Stepper [this {:stepper/keys [id steps] :as props}]
  {:ident :stepper/id
   :initial-state (fn [{:keys [id steps] :as params}]
                    {:stepper/id (or id (tempid/uuid))
                     :stepper/steps (mapv #(comp/get-initial-state Step %) steps)})
   :query [:stepper/id
           {:stepper/steps (comp/get-query Step)}]}
  (let [click-fns (comp/get-computed this :click-fns)]
    (ol {:class "select-none relative text-gray-500 border-l border-gray-200
                 dark:border-gray-700 dark:text-gray-400"}
        (for [step steps]
          (ui-step step {:onClick (get click-fns (:step/id step))})))))

(def ui-stepper (comp/computed-factory Stepper {:keyfn :stepper/id}))
