(ns se.w3t.codo.components.blueprint.modal
 (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
           #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div a i p img span button]]
              :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div a i p img span button]])

           [com.fulcrologic.fulcro.dom.events :as events]
           [se.w3t.blueprint.icons.solid :refer [x-mark]]
           
           [se.w3t.flowbite.factories :as f]))

(comp/defsc Modal [this props]
  {}
  (div {:id "authentication-modal"
        :tabindex "-1"
        :aria-hidden "true"
        :class "fixed top-0 left-0 right-0 z-50 w-full p-4 overflow-x-hidden overflow-y-auto md:inset-0
                    h-modal md:h-full bg-black items-center justify-center flex"}
    (div {:class "relative max-w-md md:h-auto"}
      (div {:class "relative bg-white rounded-lg shadow dark:bg-gray-700"}
        (button {:class "absolute top-3 right-2.5 text-gray-400 bg-transparent
                                  hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5
                                  ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
                 :onClick (:onClose props)}
          x-mark)
        (div {:class "px-6 py-6 lg:px-8"}
          (:children props))))))

(def ui-modal (comp/computed-factory Modal))
