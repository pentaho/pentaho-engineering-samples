/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2016 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

define([
  "pentaho/module!_",
  "pentaho/visual/Model"
], function(module, BaseModel) {

  "use strict";

  return BaseModel.extend({
    $type: {
      id: module.id,

      // The label may show up in menus
      label: "D3 Bar Chart",

      // Properties
      props: [
        // General properties
        {
          name: "barSize",
          valueType: "number",
          defaultValue: 30,
          isRequired: true
        },

        // Visual role properties
        {
          name: "category",
          base: "pentaho/visual/role/Property",
          fields: {isRequired: true}
        },
        {
          name: "measure",
          base: "pentaho/visual/role/Property",
          modes: [{dataType: "number"}],
          fields: {isRequired: true}
        },

        // Palette property
        {
          name: "palette",
          base: "pentaho/visual/color/PaletteProperty",
          levels: "nominal",
          isRequired: true
        }
      ]
    }
  })
  .configure();
});
