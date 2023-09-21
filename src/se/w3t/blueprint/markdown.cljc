(ns se.w3t.blueprint.markdown
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:cljs ["react-markdown" :default ReactMarkdown])
            #?(:cljs ["remark-gfm" :default remarkGfm])
            #?(:cljs ["rehype-highlight" :default rehypeHighlight])
            #?(:cljs ["highlight.js/lib/languages/clojure.js" :as clojure])))

#?(:cljs
   (do
     (def ui-markdown (interop/react-factory ReactMarkdown))

     (def languages {:clojure clojure})

     (defsc Render [_this {:keys [body]}]
       {}
       (when body
         (ui-markdown {:children body
                       :remarkPlugins [remarkGfm]
                       :rehypePlugins [(partial rehypeHighlight (clj->js {:languages languages}))]})))
     
     (def render (comp/factory Render))))
