#!/bin/bash

# todo 适配程序所在位置
JAVA=/usr/lib/jvm/java-22-jdk/bin/java
PANDOC=pandoc
EXTRACT_KEYWORD_JAR=extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar
GET_FRONT_MATTER=handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar
SET_FRONT_MATTER=handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar
UPLOAD_WORDPRESS=upload-blog/upload-wordpress/target/upload-wordpress-1.0-SNAPSHOT.jar


#recursive read_dir
read_dir(){
    for file in `ls -a $1`
    do
        if [ -d $1"/"$file ]
        then
            if [[ $file != '.' && $file != '..' ]]
            then
                read_dir $1"/"$file
            fi
        else
            echo "$1/$file"
        fi
    done
}

extract_keyword() {
  in_file=
  ret_val=
  in_file=$1
  keywords=`$JAVA -jar $EXTRACT_KEYWORD_JAR $1`
  ret_val=$?
  echo "$keywords"
  return $ret_val
}

markdown_to_html() {
  in_file=
  out_file=
  in_file=$1
  out_file=$2
  $PANDOC --mathjax \
      -f markdown \
      -t html \
      -o $out_file \
      $in_file
  return $?
}

get_md_uuid() {
  in_file=
  md_uuid=
  ret_val=
  in_file=$1
  md_uuid=`$JAVA -jar $GET_FRONT_MATTER -k uuid $in_file`
  ret_val=$?

  echo $md_uuid
  return $ret_val
}

set_front_matter_key() {
  in_file=
  key=

  in_file=$1
  key=$2

  shift 2
  values=`echo "$@"| xargs -I {} echo -n "-v {} "`
  #echo "$values"
  eval $JAVA -jar $SET_FRONT_MATTER -k $key "${values[@]}" $in_file
  return $?
}

upload_to_wordpress() {
  in_file=
  md_uuid=
  keys=
  ret_val=

  in_file=$1
  md_uuid=$2

  shift 2
  keys=`echo "$@"| xargs -I {} echo -n "-k {} "`
  eval $JAVA -jar $UPLOAD_WORDPRESS -u $md_uuid "${keys[@]}" $in_file
}

print_ret_status() {
  ret_val=
  given_msg=
  ret_val=$1
  given_msg=$2
  if [ $ret_val -eq 0 ]; then
    echo "$given_msg ... success"
  else
    echo "$given_msg ... fail"
  fi
  return $ret_val
}

main() {
  # has two args
  if [ ! -n "$1" ] || [ ! -n "$2" ] ; then
    echo "$0 <markdown_dir_or_file> <out_dir>"
    exit 1
  fi

  #get absolute path
  abs1=`readlink -f $1`
  abs2=`readlink -f $2`

  #if $1 is a file, the base_dir is the path before the file name.
  #if $1 is a dir, the base_dir is the whole $1 (without tail "/")
  base_dir=
  #if $1 is a file, the in_files is $1
  #if $1 is a dir, the in_files are all of the files in dir $1 (filtered by suffix of ".md")
  in_files=
  if [ -d $abs1 ]; then
    base_dir=${abs1%/}
    in_files=`read_dir $base_dir|grep -E "\.md"`
  elif [ -f $abs1 ]; then
    base_dir=`dirname $abs1`
    in_files=$abs1
  else
    echo "arg1 neither a directory nor a regular file"
    exit 1
  fi

  # output dir
  out_dir=${abs2%/}


  # handle the in_files
  OLD_IFS=$IFS
  IFS=$'\n'
  for in_file in $in_files
  do
    rela_file=
    rela_dir=
    file_name=
    out_file=
    # relative path of the file
    rela_file=${in_file#${base_dir}/}
    # relative path of the file's dir
    rela_dir=`dirname rela_file`
    # file name without suffix
    file_name=`basename $in_file .md`
    #echo $rela_file
    #echo $rela_dir
    #echo $file_name
    mkdir -p $out_dir/$rela_dir
    out_file=`readlink -f $out_dir/$rela_dir/$file_name.html`

    md_uuid=
    md_uuid=`get_md_uuid $in_file`
    print_ret_status $? "$in_file: Getting uuid" || continue

    if [ "$md_uuid" == "" ]; then
      md_uuid=`uuidgen`
      print_ret_status $? "$in_file: Generating new uuid" || continue

      set_front_matter_key $in_file "uuid" $md_uuid
      print_ret_status $? "$in_file: Setting uuid" || continue
    fi

    keywords=
    keywords=`extract_keyword $in_file`
    print_ret_status $? "$in_file: Extracting keywords" || continue


    keyword_names=
    keyword_scores=
    if [ "$keywords" != "" ]; then
      keyword_names=`echo "$keywords"|awk '{print $1}'`
      #echo "$keyword_names"
      set_front_matter_key $in_file "keywords" "$keyword_names"
      print_ret_status $? "$in_file: Setting keywords" || continue

      keyword_scores=`echo "$keywords"|awk '{print $2}'`
      set_front_matter_key $in_file "scores" "$keyword_scores"
      print_ret_status $? "$in_file: Setting scores" || continue
    fi

    markdown_to_html $in_file $out_file
    print_ret_status $? "$in_file => $out_file" || continue

    upload_to_wordpress $out_file $md_uuid "$keyword_names"
    print_ret_status $? "$in_file: Uploading to wordpress" || continue

    #echo
  done
  IFS=$OLD_IFS

}

main "$@"
