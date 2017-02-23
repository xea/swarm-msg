var data = [4, 8, 15, 16, 23, 42];

/*
d3.select("body").transition()
    .style("background-color", "black");
*/

let run = function() {
    d3.select("body")
        .selectAll("div")
        .data(data)
        .enter().append("p")
        .text(function(d) { return "I’m number " + d + "!"; });

    d3.select(".chart")
        .data(data)
        .enter()
        .append("div")
        .style("width", function(d) { return d * 10 + "px"; })
        .text(function(d) { return d; });
}

