(ns se.w3t.blueprint.icons.web3
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div svg path i img p a section h1 h2 text]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i svg path p a img section h1 h2 text]])
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            #?(:cljs ["react-ipfs-image" :refer [IpfsImage]])))


#?(:cljs (do
           (def ui-ipfs-image (interop/react-factory IpfsImage))

           (def ethereum
             (ui-ipfs-image {:hash "QmdwQDr6vmBtXmK2TmknkEuZNoaDqTasFdZdu3DRw8b2wt"}))

           (def polygon
             (ui-ipfs-image {:hash "QmRNqgazYuxUa5WdddFPftTWiP3KwzBMgV9Z19QWnLMETc"}))

           (def optimism
             (ui-ipfs-image {:hash "bafkreiefsslcrr473mmyxc3bnbzrovyxbdqw47p54wlufccumpkbumhd44"}))

           (def arbitrum
             (ui-ipfs-image {:hash "QmR148wDHPvVANmsPngUVcY4mfgeVk9Jn2xau1XRpoXEAo"}))

           (def hardhat
             (img {:src "https://plugins.jetbrains.com/files/18551/301651/icon/pluginIcon.png"})))
   :clj (do
          (def  ui-ipfs-image nil)
          (def ethereum nil)
          (def polygon nil)
          (def optimism nil)
          (def arbitrum nil)
          (def hardhat nil)))
