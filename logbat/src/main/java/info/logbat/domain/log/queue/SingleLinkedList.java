package info.logbat.domain.log.queue;

public class SingleLinkedList<E> {

    Node<E> first;
    Node<E> lastHolder;

    public SingleLinkedList() {
        lastHolder = new Node<>(null, null);
        first = lastHolder;
    }

    private void linkLast(E e) {
        Node<E> newLastHolder = new Node<>(null, null);

        // 1. add next node
        lastHolder.next = newLastHolder;
        // 2. set item
        lastHolder.item = e;

        lastHolder = newLastHolder;
    }

    private E unlinkFirst() {
        // 1. get first element
        E element = first.item;

        // first == lastHolder
        if (first.item == null) {
            return null;
        }

        // 2. get next node
        // if element is not null, next node should not be null
        Node<E> next = first.next;
        first.item = null;
        first.next = null; // help GC
        first = next;
        return element;
    }


    public boolean isEmpty() {
        return first.item == null;
    }

    public E poll() {
        return unlinkFirst();
    }

    public void addAll(Iterable<? extends E> c) {
        for (E e : c) {
            linkLast(e);
        }
    }

    private static class Node<E> {
        volatile E item;
        volatile Node<E> next;

        Node(E element, Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }
}
