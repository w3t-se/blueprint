(ns se.w3t.blueprint.message
 (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
           #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div a i p img span button]]
              :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div a i p img span button]])
           [com.fulcrologic.fulcro.dom.events :as events]
           [se.w3t.blueprint.icons.solid :as si]
           [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
           [com.fulcrologic.fulcro.algorithms.merge :as merge]
           [com.fulcrologic.fulcro.algorithms.tempid :refer [uuid tempid]]
           #?(:cljs [helins.timer :as timer])
           [se.w3t.flowbite.factories :as f]))

(def message-type {:error {:color "failure"
                          :icon si/exclamation-circle}
                   :warning {:color "warning"
                             :icon si/exclamation-triangle}
                   :info {:color "success"
                          :icon si/information-circle}})

(comp/defsc Message [this {:message/keys [id content type] :as props}]
  {:ident :message/id
   :initial-state (fn [{:keys [id content type]}]
                    {:message/id (or id (tempid))
                     :message/content (or content nil)
                     :message/type (or type :info)})
   :query [:message/id :message/content :message/type]}
  (if content
    (let [{:keys [color]} (get message-type type)]
      (div {:class "transition transform absolute bottom-0 bottom-10 right-20
                  position-absolute"}
        (f/ui-alert {:color color}
                    (dom/span {:class "text-md"} content))))))

#?(:cljs
   (defmutation display-message [{:keys [content & type]}]
     (remote [env] false)
     (action [{:keys [app state]}]
       (let [id (tempid)]
         (merge/merge-component! app Message (comp/get-initial-state Message {:id id :content content :type type})
                                 :replace [:root/message])
         (timer/in timer/main-thread 3000
                   #(comp/transact! app `[(se.w3t.blueprint.mutations/remove-entity {:id ~[:message/id id] :send-remote false})]))))))

(def ui-message (comp/computed-factory Message))
