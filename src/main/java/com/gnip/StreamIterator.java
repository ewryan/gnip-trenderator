package com.gnip;

import java.io.*;
import java.util.Iterator;

public class StreamIterator implements Iterator<String> {
        private final Reader reader;
        private String cachedLine;

        public StreamIterator(InputStream inputStream) throws UnsupportedEncodingException {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        }

        public boolean hasNext() {
            return cachedLine != null || fill();
        }

        public String next() {
            if (hasNext()) {
                return getAndResetCachedLine();
            }

            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported for tweet iterators.");
        }

        private String getAndResetCachedLine() {
            String returnVal = cachedLine;
            cachedLine = null;
            return returnVal;
        }

        private boolean fill() {
            StringBuilder sb = new StringBuilder(4096);
            boolean startedObject = false;

            while (true) {
                try {
                    int currentChar = reader.read();

                    if (currentChar == '\n' && !startedObject) {
                        continue;
                    } else if (currentChar == ' ' && !startedObject) {
                        continue;
                    } else if (currentChar == '\r' && !startedObject) {
                        continue;
                    } else if (currentChar == -1) {

                        closeQuietly(reader);
                        if (sb.length() > 0) {
                            cachedLine = sb.toString();
                            return true;
                        } else {
                            return false;
                        }
                    } else if (currentChar == '\r') {
                        cachedLine = sb.toString();
                        return cachedLine.length() > 0;
                    } else if (currentChar == '{') {
                        startedObject = true;
                        sb.append((char) currentChar);
                    } else {
                        sb.append((char) currentChar);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading tweets.");
                    e.printStackTrace();
                    closeQuietly(reader);
                    return false;
                }
            }
        }

        private void closeQuietly(Reader reader) {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
