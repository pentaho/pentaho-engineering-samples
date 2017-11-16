/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2016 - 2017 Hitachi Vantara. All rights reserved.
 */
define([
  "module",
  "d3",
  "css!./css/view-d3"
], function(module, d3) {
  "use strict";

  return [
    "pentaho/visual/base/view",
    "./model",
    "pentaho/visual/action/execute",
    "pentaho/visual/action/select",
    function(BaseView, BarModel, ExecuteAction, SelectAction) {
      // Create the Bar View subclass
      var BarView = BaseView.extend({
        $type: {
          id: module.id,
          props: [
            // Specialize the inherited model property to the Bar model type
            {
              name: "model",
              valueType: BarModel
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
          // Part 1

          // The Bar model
          var model = this.model;

          var dataTable = model.data;

          // The name of the data attributes that are mapped to the visual roles
          var categoryAttribute = model.category.attributes.at(0).name;
          var measureAttribute = model.measure.attributes.at(0).name;

          // Their column indexes in the data table.
          var categoryColumn = dataTable.getColumnIndexByAttribute(categoryAttribute);
          var measureColumn = dataTable.getColumnIndexByAttribute(measureAttribute);

          // Build a list of scenes, one per category
          var scenes = this.__buildScenes(dataTable, categoryColumn, measureColumn);

          // The div where rendering takes place
          var container = d3.select(this.domContainer);

          // Part 2
          container.selectAll("*").remove();

          var margin = { top: 50, right: 30, bottom: 30, left: 75 };

          // Note use of the view's width and height properties
          var width = this.width - margin.left - margin.right;
          var height = this.height - margin.top - margin.bottom;

          var x = d3.scaleBand().rangeRound([0, width]).padding(0.1);
          var y = d3.scaleLinear().rangeRound([height, 0]);

          x.domain(scenes.map(function (d) { return d.categoryLabel; }));
          y.domain([0, d3.max(scenes, function (d) { return d.measure; })]);

          var svg = container.append("svg")
            .attr("width", this.width)
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
          var barWidth = Math.min(model.barSize, bandWidth);
          var barOffset = bandWidth / 2 - barWidth / 2 + 0.5;

          var selectColor = function(d) {
            return model.palette.colors.at(d.rowIndex % model.palette.colors.count).value;
          };

          var bar = g.selectAll(".bar")
            .data(scenes)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("fill", selectColor)
            .attr("stroke", selectColor)
            .attr("x", function (d) { return x(d.categoryLabel) + barOffset; })
            .attr("y", function (d) { return y(d.measure); })
            .attr("width", barWidth)
            .attr("height", function (d) { return height - y(d.measure); });

          // Part 3
          var view = this;

          bar.on("dblclick", function(d) {
            // A filter that would select the data that the bar visually represents
            var filterSpec = { _: "=", property: categoryAttribute, value: d.category };

            // Create the action.
            var action = new ExecuteAction({ dataFilter: filterSpec });

            // Dispatch the action through the view.
            view.act(action);
          });

          // Part 4
          bar.on("click", function(d) {
            // A filter that would select the data that the bar visually represents
            var filterSpec = { _: "=", property: categoryAttribute, value: d.category };

            // Create the action.
            var action = new SelectAction({dataFilter: filterSpec, selectionMode: event.ctrlKey || event.metaKey ? "toggle" : "replace"});

            // Dispatch the action through the view.
            console.log(action);
            view.act(action);
          });

          // Part 5
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

          for (var i = 0, R = dataTable.getNumberOfRows(); i < R; i++) {
            scenes.push({
              category: dataTable.getValue(i, categoryColumn),
              categoryLabel: dataTable.getFormattedValue(i, categoryColumn),
              measure: dataTable.getValue(i, measureColumn),
              rowIndex: i
            });
          }

          return scenes;
        }
      });

      return BarView;
    }
  ];
});
