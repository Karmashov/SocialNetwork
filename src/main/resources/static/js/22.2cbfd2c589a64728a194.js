webpackJsonp([22],{"N9p+":function(e,s,t){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var i=t("Dd8w"),n=t.n(i),r=(t("IcnI"),t("NYxO")),a=t("UBpT"),d=t("CqtB"),f={name:"Friends",components:{FriendsPossible:a.a,FriendsBlock:d.a},data:function(){return{first_name:""}},computed:n()({},Object(r.c)("profile/friends",["getResultById"]),{friends:function(){var e=this;return 0===this.first_name.length?this.getResultById("friends"):this.getResultById("friends").filter(function(s){return-1!==s.first_name.toLowerCase().indexOf(e.first_name.toLowerCase())||-1!==s.last_name.toLowerCase().indexOf(e.first_name.toLowerCase())})}}),methods:n()({},Object(r.b)("profile/friends",["apiFriends"])),beforeRouteEnter:function(e,s,t){t(function(e){e.apiFriends()})}},l={render:function(){var e=this,s=e.$createElement,t=e._self._c||s;return t("div",{staticClass:"friends inner-page"},[t("div",{staticClass:"inner-page__main"},[t("div",{staticClass:"friends__header"},[t("h2",{staticClass:"friends__title"},[e._v("Мои друзья")]),t("div",{staticClass:"friends__search"},[t("div",{staticClass:"friends__search-icon"},[t("simple-svg",{attrs:{filepath:"/static/img/search.svg"}})],1),t("input",{directives:[{name:"model",rawName:"v-model",value:e.first_name,expression:"first_name"}],staticClass:"friends__search-input",attrs:{placeholder:"Начните вводить имя друга..."},domProps:{value:e.first_name},on:{input:function(s){s.target.composing||(e.first_name=s.target.value)}}})])]),t("div",{staticClass:"friends__list"},e._l(e.friends,function(e){return t("friends-block",{key:e.id,attrs:{friend:"friend",info:e}})}),1)]),t("div",{staticClass:"inner-page__aside"},[t("friends-possible")],1)])},staticRenderFns:[]},o=t("VU/8")(f,l,!1,null,null,null);s.default=o.exports}});
//# sourceMappingURL=22.2cbfd2c589a64728a194.js.map