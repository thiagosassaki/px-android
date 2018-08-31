var old_v2_version = "2.5.4";
var old_v3_version = "3.8.0";
var new_v4_version = "4.0.0";

var old_v2_list = document.getElementsByClassName("oldV2Version");
var old_v3_list = document.getElementsByClassName("oldV3Version");
var new_v4_list = document.getElementsByClassName("newV4Version");

for (i = 0; i < old_v2_list.length; i++) {
  old_v2_list[i].innerHTML = old_v2_version;
}
for (i = 0; i < old_v3_list.length; i++) {
  old_v3_list[i].innerHTML = old_v3_version;
}

for (i = 0; i < new_v4_list.length; i++) {
  new_v4_list[i].innerHTML = new_v4_version;
}
