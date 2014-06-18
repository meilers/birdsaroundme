package com.sobremesa.birdwatching.synchronizers.preprocessors;

import com.sobremesa.birdwatching.models.remote.BaseRemoteModel;

import java.util.List;

/**
 * Created by Michael on 2014-03-11.
 */
public abstract class BasePreProcessor <T extends BaseRemoteModel> {

    public abstract void preProcessRemoteRecords(List<T> records);

}