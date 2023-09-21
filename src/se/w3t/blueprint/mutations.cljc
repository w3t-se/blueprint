(ns se.w3t.blueprint.mutations
  (:require [com.wsscode.pathom.connect :as pc]
            [datomic.client.api :as d]
            [taoensso.timbre :as log]))

(defn- env->db [env]
  (some-> env (get-in [com.fulcrologic.rad.database-adapters.datomic-options/databases :production]) (deref)))

#?(:cljs (defmutation remove-entity [{:keys [id & send-remote]}]
           (action [{:keys [app state]}]
             (swap! state normalized-state/remove-entity id))
           (remote [env] send-remote)
           (websockets [env] send-remote))
   
   :clj (pc/defmutation remove-entity [env {:keys [id]}]
          {}
          (if-let [db (env->db env)]
            (d/transact db {:tx-data [[:db/retractEntity (second id)]]}))
          {}))

