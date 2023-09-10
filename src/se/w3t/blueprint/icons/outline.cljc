(ns se.w3t.blueprint.icons.outline
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div svg path i p a section h1 h2 text]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i svg path p a section h1 h2 text]])))

(def bookmark (dom/svg
                 {:xmlns "http://www.w3.org/2000/svg",
                  :fill "none",
                  :viewBox "0 0 24 24",
                  :stroke-width 1.5,
                  :stroke "currentColor",
                  :class "w-6 h-6"}
               (dom/path
                   {:stroke-linecap "round",
                    :stroke-linejoin "round",
                    :d
                    "M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z"})))
