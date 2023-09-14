(ns se.w3t.blueprint.transition
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            #?(:cljs ["@headlessui/react$Transition" :as Transition])))

#?(:cljs
   (def ui-transition (interop/react-factory Transition))
   :clj (def ui-transition nil))
