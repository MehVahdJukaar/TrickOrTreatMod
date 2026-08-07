misc fixes

- Fixed #72 (Autumnity Turkey Egg ignoring the egg-splatter-off setting)
- Fixed #81 (Caverns and Chasms compat issue)
- Fixed #77 (crash on load with Quark installed)
- Fixed #80 (corn crate missing c:storage_blocks tags)
- Fixed #82 (compat jack o' lanterns had a missing top texture, were invisible as items and turned faceless when
  lighting a plain carved pumpkin)
- Fixed #71 (pixel outline when carving with a sword showed up on the top face instead of the carved one)
- Fixed the compat jack o' lantern loot tables erroring on load when their mods aren't installed (same as #58)
- Fixed #11, #74 and part of #63: modded eggs now splatter through the new `#hauntedharvest:splatterable_eggs` item tag,
  which defaults to `#c:eggs`. Like vanilla eggs they don't splatter when they hatch instead
