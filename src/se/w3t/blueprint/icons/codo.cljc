(ns se.w3t.blueprint.icons.codo
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div svg path i p a section h1 h2 text]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i svg path p a section h1 h2 text]])))

(def requirements-planner
  (dom/svg
 {:fill "currentColor",
  :version 1.1,
  :viewBox "0 0 36.4708 31.7083",
  :xmlns "http://www.w3.org/2000/svg"}
 (dom/g
  {:transform "translate(-46.7765 -38.4851)"}
  (dom/g
   {:transform "matrix(.205243 0 0 .205243 122.114 -107.988)"}
   (dom/rect
    {:x "-363.813",
     :y 716.546,
     :width 104.5,
     :height 45.2004,
     :rx 23.2042,
     :ry 22.6002})
   (dom/path
    {:d
     "m-240.994 728.231 10.8515 14.061 30.0599-25.0203 9.36441 10.7833-41.5232 35.544-21.7666-27.0022z"})
   (dom/rect
    {:x "-363.813",
     :y 768.164,
     :width 104.5,
     :height 45.2004,
     :rx 23.2042,
     :ry 22.6002})
   (dom/path
    {:d
     "m-240.995 778.559 10.8515 14.061 30.0599-25.0203 9.36443 10.7833-41.5232 35.544-21.7666-27.0022z"})
   (dom/rect
    {:x "-363.813",
     :y 819.781,
     :width 104.5,
     :height 45.2004,
     :rx 23.2042,
     :ry 22.6002})
   (dom/path
    {:d
     "m-240.995 828.888 10.8515 14.061 30.06-25.0203 9.36445 10.7833-41.5233 35.544-21.7666-27.0022z"})))))

(def timeline-planner
  (dom/svg
 {:fill "currentColor",
  :version 1.1,
  :viewBox "0 0 36.4708 31.7083",
  :xmlns "http://www.w3.org/2000/svg"}
 (dom/g
  {:transform "translate(-46.7765 -38.4851)"}
  (dom/g
   {:transform "matrix(.205243 0 0 .205243 122.114 -107.988)"}
   (dom/rect
    {:x "-364.63",
     :y 768.301,
     :width 104.5,
     :height 45.2004,
     :rx 23.2042,
     :ry 22.6002})
   (dom/rect
    {:x "-323.378",
     :y 820.511,
     :width 104.5,
     :height 45.2004,
     :rx 23.2042,
     :ry 22.6002})
   (dom/rect
    {:x "-296.306",
     :y 716.092,
     :width 104.5, 
     :height 45.2004, 
     :rx 23.2042, 
     :ry 22.6002})))))
