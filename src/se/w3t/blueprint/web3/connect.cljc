(ns se.w3t.blueprint.web3.connect
 (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
           #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div h3 a i p li ol img span button]]
              :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div a li ol h3 i p img span button]])

           [com.fulcrologic.fulcro.dom.events :as events]
           [se.w3t.codo.components.blueprint.icons.solid :as s :refer [check clipboard-document-list cube]]
           [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
           [se.w3t.flowbite.factories :as f]
           [applied-science.js-interop :as j]
           [se.w3t.blueprint.icons.web3 :refer [ethereum polygon optimism arbitrum hardhat]]
           #?(:cljs ["wagmi" :as wagmi :refer [useConnect useAccount useDisconnect useEnsAvatar
                                                     useEnsName useNetwork useSwitchNetwork]])))

#?(:cljs (comp/defsc Connect [this props]
           {;; :ident :connect/id
            ;; :initial-state (fn [{:keys [id] :as params}] {:connect/id (or id (tempid/uuid))})
            ;; :query [:connect/id]
            :use-hooks? true}
           (let [{:keys [connectAsync connect connectors error isLoading pendingConnector]} (js->clj (useConnect) :keywordize-keys true)
                 {:keys [address connector isConnected]} (js->clj (useAccount) :keywordize-keys true)
                 {:keys [disconnect]} (js->clj (useDisconnect) :keywordize-keys true)
                 ensName (js->clj (useEnsName address) :keywordize-keys true)
                 ensAvatar (js->clj (useEnsAvatar address) :keywordize-keys true)
                 on-click (comp/get-computed this :on-click)]
             (if isConnected
               (div {}
                 #_(div (str (js->clj (useConnect) :keywordize-keys true)))
                 #_(div {} (str "Connected to: " (str connector.chains)))
                 #_(img {:src ensAvatar :alt "ENS Avatar"})
                 (div {} (str (:data ensAvatar)))
                 #_(div {} (str (if ensName ensName (str address))))
                 (f/ui-button {:onClick disconnect} "Disconnect"))
               (f/ui-dropdown {:label "Connect Wallet"}
                   (for [c connectors]
                     (f/ui-button {:onClick #(do (println c.options)
                                               (.then (connectAsync (clj->js {:connector c}))
                                                      (fn [_] (if on-click (on-click)))))} (str c.name))))))))

#?(:clj (def ui-connect nil)
   :cljs (def ui-connect (comp/computed-factory (comp/memo Connect))))

(def chain-icon {"Polygon" polygon
                 "Ethereum" ethereum
                 "Optimism" optimism
                 "Arbitrum One" arbitrum
                 "Hardhat" hardhat})
#?(:cljs
   (comp/defsc ChainMenu [this props]
     {:use-hooks? true}
     (let [{:keys [chain chains]} (js->clj (useNetwork) :keywordize-keys true)
           {:keys [chains error isLoading pendingChainId switchNetwork]} (js->clj (useSwitchNetwork) :keywordize-keys true)]
       (div {:class "self-center"}
         (f/ui-dropdown {:color "grey"
                         :label (div {:class "flex"}
                                  (div :.w-5.h-5.mr-2 (get chain-icon (:name chain)))
                                  (:name chain))}
           (for [chain chains]
             (f/ui-dropdown-item {:onClick #(switchNetwork (:id chain))} (div {:class "flex py-1"}
                                                                           (div :.w-5.h-5.mr-2 (get chain-icon (:name chain)))
                                                                           (:name chain)))))))))

#?(:cljs (def ui-chain-menu (comp/computed-factory (comp/memo ChainMenu)))
   :clj (def ui-chain-menu nil))
