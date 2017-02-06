(require 'cljs.build.api)

(cljs.build.api/build "src" {:output-to "src/main/resources/public/js/main.js"})