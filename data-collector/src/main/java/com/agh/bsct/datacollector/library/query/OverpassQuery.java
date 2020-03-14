package com.agh.bsct.datacollector.library.query;

import com.agh.bsct.datacollector.library.union.Recurse;
import com.agh.bsct.datacollector.library.union.output.OutputFormat;
import com.agh.bsct.datacollector.library.union.output.OutputModificator;
import com.agh.bsct.datacollector.library.union.output.OutputOrder;
import com.agh.bsct.datacollector.library.union.output.OutputVerbosity;

import java.util.Locale;

import static com.agh.bsct.datacollector.library.union.output.OutputModificator.CENTER;
import static com.agh.bsct.datacollector.library.union.output.OutputOrder.QT;
import static com.agh.bsct.datacollector.library.union.output.OutputVerbosity.BODY;


/**
 * The main query class to create and build requests using the Overpass QL
 */
public class OverpassQuery extends AbstractOverpassQuery {
    public OverpassQuery() {
        super();
    }

    OverpassQuery(OverpassQueryBuilder builder) {
        super(builder);
    }

    /**
     * Controls the output format used to return OSM data.
     *
     * @param outputFormat the OutputFormat to use
     * @return the current OverpassQuery object
     * @see <a href="http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Output_Format_.28out.29">
     * http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Output_Format_.28out.29</a>
     */
    public OverpassQuery format(OutputFormat outputFormat) {
        builder.setting("out", outputFormat.toString().toLowerCase());

        return this;
    }

    /**
     * Sets the maximum allowed runtime for the query in seconds.
     *
     * @param timeout the maximum allowed runtime in seconds
     * @return the current OverpassQuery object
     * @see <a href="http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#timeout">
     * http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#timeout</a>
     */
    public OverpassQuery timeout(int timeout) {
        builder.setting("timeout", "" + timeout);

        return this;
    }

    /**
     * Defines a global bounding box that is then implicitly added to all queries (unless they specify a different explicit bounding box)
     *
     * @param southernLat the southern latitude
     * @param westernLon  the western longitude
     * @param northernLat the northern latitude
     * @param easternLon  the eastern longitude
     * @return the current OverpassQuery object
     */
    public OverpassQuery boundingBox(double southernLat, double westernLon, double northernLat, double easternLon) {
        builder.append(String.format(Locale.US, "[bbox:%s,%s,%s,%s]",
                southernLat,
                westernLon,
                northernLat,
                easternLon
        ));

        return this;
    }

    /**
     * Creates a map query to embed its output in the current query.
     *
     * @return the map query
     */
    public OverpassFilterQuery filterQuery() {
        return new OverpassFilterQuery(this);
    }

    /**
     * Appends the print (out) action to the query with default parameters:
     *
     * <ul>
     * <li>verbosity: {@link OutputVerbosity#BODY }</li>
     * <li>modificator: {@link OutputModificator#CENTER }</li>
     * <li>order: {@link OutputOrder#QT }</li>
     * </ul>
     *
     * @param limit maximum number of elements to return
     * @return the current OverpassQuery object
     * @see #output(OutputVerbosity, OutputModificator, OutputOrder, int)
     */
    public OverpassQuery output(int limit) {
        return output(BODY, CENTER, QT, limit);
    }

    /**
     * Appends a <i>;</i> character and the print (out) action to the query.
     *
     * @param verbosity   degree of output verbosity (see {@link OutputVerbosity })
     * @param modificator output modificator for derived information (see {@link OutputModificator })
     * @param order       sort order (see {@link OutputOrder })
     * @param limit       maximum number of elements to return
     * @return the current OverpassQuery object
     * @see <a href="http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Print_.28out.29">
     * http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Print_.28out.29</a>
     */
    public OverpassQuery output(OutputVerbosity verbosity, OutputModificator modificator, OutputOrder order, int limit) {
        builder.append(String.format("; out %s %s %s %s",
                verbosity.toString().toLowerCase(),
                modificator.toString().toLowerCase(),
                order.toString().toLowerCase(),
                limit
                )
        );

        return this;
    }

    /*
     * Here starts modifications of original code from github.
     */

    /**
     * Appends the recurse to output
     *
     * @param recurse recurse up or down
     * @return the current OverpassQuery object
     */
    public OverpassQuery output(Recurse recurse) {
        builder.append(";").append(recurse.toString());
        return this;
    }

    /**
     * Appends a <i>;</i> character and the print (out) action to the query.
     *
     * @param outputVerbosity degree of output verbosity (see {@link OutputVerbosity })
     */
    public OverpassQuery output(OutputVerbosity outputVerbosity) {
        builder.append(String.format("; out %s",
                outputVerbosity.toString().toLowerCase()
                )
        );

        return this;
    }

    /**
     * Appends a <i>;</i> character and the print (out) action to the query.
     *
     * @param outputVerbosity degree of output verbosity (see {@link OutputVerbosity })
     * @param outputOrder       sort order (see {@link OutputOrder })
     */
    public OverpassQuery output(OutputVerbosity outputVerbosity, OutputOrder outputOrder) {
        builder.append(String.format("; out %s %s",
                outputVerbosity.toString().toLowerCase(),
                outputOrder.toString().toLowerCase()
                )
        );

        return this;
    }

    /*
     * Here ends modifications of original code from github.
     */

    /**
     * Closes the current query with the character <i>;</i> and returns the output as a string.
     *
     * @return the query as string
     */
    @Override
    public String build() {
        builder.append(";");

        return builder.build();
    }
}
