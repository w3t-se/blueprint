(ns se.w3t.blueprint.search-input
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div a i p img span button input form label]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div a i p img span button input form label]])

            [se.w3t.blueprint.icons.solid :refer [magnifying-glass]]
            [com.fulcrologic.fulcro.dom.events :as events]))

(comp/defsc SearchInput [this props]
  {:initLocalState (fn [_] {:search ""})}
  (let [onSubmit (:onSubmit props)]
    (form {:className "mr-3"
           :onSubmit (fn [e]
                       (.preventDefault e)
                       (onSubmit e this))}
      (label {:htmlFor "default-search"
              :className "mb-2 text-sm font-medium text-zinc-900 sr-only dark:text-white"}
        "Search")
      (div {:className "relative"}
        (div {:className "absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none"}
          magnifying-glass)
        (input {:type "search"
                :id "default-search"
                :className "dark:bg-black bg-white block w-full h-11 p-4 pl-10 text-sm text-gray-900 border
                                border-gray-300 rounded-lg
                                focus:ring-blue-500 focus:border-blue-500
                                dark:border-gray-700 dark:placeholder-gray-400 dark:text-white
                                dark:focus:ring-blue-500 dark:focus:border-blue-500"
                :placeholder "Search ..."
                :onChange #(comp/set-state! this {:search (events/target-value %)})
                :value (comp/get-state this :search)})))))

(def ui-search-input (comp/computed-factory SearchInput))
