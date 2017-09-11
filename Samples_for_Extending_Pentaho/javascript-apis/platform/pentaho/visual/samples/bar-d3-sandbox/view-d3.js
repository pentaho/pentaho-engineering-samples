/*!
 * Copyright 2010 - 2017 Pentaho Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
define([
  "module",
  "pentaho/visual/base/view",
  "./model",
  "pentaho/visual/action/execute",
  "pentaho/visual/action/select",
  "d3",
  "css!./css/view-d3",
], function(module, baseViewFactory, barModelFactory, executeActionFactory, selectActionFactory, d3) {

  "use strict";

  // Return the View factory function.
  return function(context) {

    // Obtain the base View class from the context, given the base View's factory function.
    var BaseView = context.get(baseViewFactory);

    // Create the Bar View subclass
    var BarView = BaseView.extend({
      $type: {
        id: module.id,
        props: [
          // Specialize the inherited model property to the Bar model type
          {
            name: "model",
            valueType: barModelFactory
          }
        ]
      },

      /**
       * Performs a full update of the visualization.
       *
       * The D3 code was adapted from https://bl.ocks.org/mbostock/3885304.
       *
       * @return {Promise} A promise that is resolved when the update operation has completed or, _nully_,
       * if it completed synchronous and with no errors.
       *
       * @protected
       * @override
       */
      _updateAll: function() {

        // The Bar model
        var model = this.model;

        var dataTable = model.data;

        // The name of the data attributes that are mapped to the visual roles.
        var categoryAttribute = model.category.attributes.at(0).name;
        var measureAttribute = model.measure.attributes.at(0).name;

        // Their column indexes in the data table.
        var categoryColumn = dataTable.getColumnIndexByAttribute(categoryAttribute);
        var measureColumn = dataTable.getColumnIndexByAttribute(measureAttribute);

        // Build a list of scenes, one per category.
        var scenes = this.__buildScenes(dataTable, categoryColumn, measureColumn);

        // The div where rendering takes place.
        var container = d3.select(this.domContainer);

        // ---

        // TODO: D3 - please look away...
        container.selectAll("*").remove();

        var margin = {top: 50, right: 30, bottom: 30, left: 75};

        // Note use of the view's width and height properties
        var width  = this.width  - margin.left - margin.right;
        var height = this.height - margin.top  - margin.bottom;

        var x = d3.scaleBand().rangeRound([0, width]).padding(0.1);
        var y = d3.scaleLinear().rangeRound([height, 0]);

        x.domain(scenes.map(function(d) { return d.categoryLabel; }));
        y.domain([0, d3.max(scenes, function(d) { return d.measure; })]);

        var svg = container.append("svg")
            .attr("width",  this.width)
            .attr("height", this.height);

        // Title
        var title = dataTable.getColumnLabel(measureColumn) +
                    " per " +
                    dataTable.getColumnLabel(categoryColumn);

        svg.append("text")
            .attr("class", "title")
            .attr("y", margin.top / 2)
            .attr("x", this.width / 2)
            .attr("dy", "0.35em")
            .attr("text-anchor", "middle")
            .text(title);

        var g = svg.append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        // X axis
        g.append("g")
            .attr("class", "axis axis-x")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Y axis
        g.append("g")
            .attr("class", "axis axis-y")
            .call(d3.axisLeft(y).ticks(10));

        // Bars
        var bandWidth = x.bandwidth();
        var barWidth  = Math.min(model.barSize, bandWidth);
        var barOffset = bandWidth / 2 - barWidth / 2 + 0.5;

        var bar = g.selectAll(".bar")
            .data(scenes)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) { return x(d.categoryLabel) + barOffset; })
            .attr("y", function(d) { return y(d.measure); })
            .attr("width", barWidth)
            .attr("height", function(d) { return height - y(d.measure); });

        var view = this;
        var context = this.$type.context;

        bar.on("dblclick", function(d) {
          // A filter that would select the data that the bar visually represents.
          var filterSpec = {_: "=", property: categoryAttribute, value: d.category};

          // Create the action.
          var ExecuteAction = context.get(executeActionFactory);
          var action = new ExecuteAction({dataFilter: filterSpec});

          // Dispatch the action through the view.
          view.act(action);
        });

        bar.on("click", function(d) {
          // A filter that would select the data that the bar visually represents.
          var filterSpec = {_: "=", property: categoryAttribute, value: d.category};

          var SelectAction = context.get(selectActionFactory);
          var action = new SelectAction({dataFilter: filterSpec, selectionMode: 'replace'});

          view.act(action);
        });

        bar.classed("selected", function(d) {
          var sf = view.selectionFilter;
          return !!sf && dataTable.filterMatchesRow(sf, d.rowIndex);
        });
      },

      /**
       * Builds the data array that feeds D3 given a DataTable and
       * the indexes of the columns mapped to the `category` and `measure` visual roles.
       *
       * @param {pentaho.data.ITable} dataTable - The data table.
       * @param {number} categoryColumn - The index of the column of the attribute mapped to the `category` visual role.
       * @param {number} measureColumn - The index of the column of the attribute mapped to the `measure` visual role.
       * @return {Array.<{category: string, measure: number}>} The scenes array.
       * @private
       */
      __buildScenes: function(dataTable, categoryColumn, measureColumn) {

        var scenes = [];

        for(var i = 0, R = dataTable.getNumberOfRows(); i < R; i++) {
          scenes.push({
            category:      dataTable.getValue(i, categoryColumn),
            categoryLabel: dataTable.getFormattedValue(i, categoryColumn),
            measure:       dataTable.getValue(i, measureColumn),
            rowIndex:      i
          });
        }

        return scenes;
      }
    });

    return BarView;
  };
});
