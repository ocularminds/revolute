/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package revolute;

/**
 *
 * @author Dev.io
 */
public interface Processor {    
    
    static final String SPACE = " ";

    Fault process(Transfer transfer);

    default boolean isNotaNumber(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}