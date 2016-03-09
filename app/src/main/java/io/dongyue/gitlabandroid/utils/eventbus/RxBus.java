package io.dongyue.gitlabandroid.utils.eventbus;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import io.dongyue.gitlabandroid.utils.eventbus.events.UniqueEvent;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Brotherjing on 2016/3/9.
 */
public class RxBus {

    private static RxBus bus;
    //private Subject<T,T> subject;
    private ConcurrentHashMap<Class<?>,Subject> subjectHashMap;

    public static RxBus getBus(){
        if(bus==null){
            bus = new RxBus();
        }
        return bus;
    }

    private RxBus(){
        //subject = new SerializedSubject<>(PublishSubject.<T>create());
        subjectHashMap = new ConcurrentHashMap<>();
    }

    public <E> void post(E event){
        //subject.onNext(event);
        Class clazz = event.getClass();
        Subject<E,E> subject = subjectHashMap.get(clazz);
        if(subject==null){
            subject = new SerializedSubject<>(PublishSubject.<E>create());
            subjectHashMap.put(clazz,subject);
        }
        subject.onNext(event);
    }

    public <E> Observable<E> observeEvents(Class<E> eventClass) {
        Subject<E,E> subject = subjectHashMap.get(eventClass);
        if(subject==null){
            subject = new SerializedSubject<>(PublishSubject.<E>create());
            subjectHashMap.put(eventClass,subject);
        }
        if(eventClass.isAssignableFrom(UniqueEvent.class)&&subject.hasObservers()){
            return Observable.empty();//if the event should only be received by one observer
        }
        return subject.ofType(eventClass);//pass only events of specified type, filter all other
    }

}
