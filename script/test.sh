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
  local OLD_IFS=$IFS
  local IFS=$'\n'
  local blog_ignore=
  # skip files
  if [ -f "$1/blog.ignore" ]; then
    blog_ignore=`cat "$1/blog.ignore"`
  fi
  for file in `ls -a $1`
  do
    # skip files
    echo "$blog_ignore" |grep -wq $file
    if [ $? -eq 0 ]; then
      continue
    fi
    # dir or regular file
    if [ -d $1"/"$file ]; then
      if [[ $file != '.' && $file != '..' ]]; then
          read_dir "$1/$file"
      fi
    else
      echo "$1/$file"
    fi
  done
  IFS=$OLD_IFS
}

extract_keyword() {
  local in_file=
  local ret_val=
  local keywords=
  in_file=$1
  keywords=`$JAVA -jar $EXTRACT_KEYWORD_JAR $1`
  ret_val=$?
  echo "$keywords"
  return $ret_val
}

markdown_to_html() {
  local in_file=
  local out_file=
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
  local in_file=
  local md_uuid=
  local ret_val=
  in_file="$1"
  md_uuid=`$JAVA -jar $GET_FRONT_MATTER -k uuid "$in_file"`
  ret_val=$?

  echo $md_uuid
  return $ret_val
}

set_front_matter_key() {
  local in_file=
  local ptr_in_file=
  local key=
  local values=

  in_file="$1"
  ptr_in_file=in_file
  key=$2
  #echo "-----### $in_file"

  shift 2
  values=`echo "$@"| xargs -I {} echo -n "-v {} "`
  #echo "script: set_front_matter_key: values: ${values[@]}"
  #eval echo "script: set_front_matter_key: $ptr_in_file: $ptr_in_file"
  #eval echo "script: set_front_matter_key: \$$ptr_in_file: \$$ptr_in_file"
  #eval echo "script: set_front_matter_key: $in_file: $in_file"

  # eval would scan twice, values array would expand to several args,
  # but $in_file may have space, be careful.(So I use a pointer to avoid side effect of eval)
  eval $JAVA -jar $SET_FRONT_MATTER -k $key ${values[@]} "\$$ptr_in_file"
  return $?
}

upload_to_wordpress() {
  local in_file=
  local ptr_in_file=
  local md_uuid=
  local keys=

  in_file=$1
  ptr_in_file=in_file
  md_uuid=$2

  shift 2
  keys=`echo "$@"| xargs -I {} echo -n "-k {} "`
  eval $JAVA -Dorg.slf4j.simpleLogger.defaultLogLevel=error -jar $UPLOAD_WORDPRESS -u $md_uuid "${keys[@]}" "\$$ptr_in_file"
  return $?
}

print_ret_status() {
  local ret_val=
  local given_msg=
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
    in_files=`read_dir $base_dir`
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
  local OLD_IFS=$IFS
  local IFS=$'\n'
  for in_file in `echo "$in_files"|grep -E "\.md$"`
  do
    echo $in_file
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
    md_uuid=`get_md_uuid "$in_file"`
    print_ret_status $? "$in_file: Getting uuid" || continue

    if [ "$md_uuid" == "" ]; then
      md_uuid=`uuidgen`
      print_ret_status $? "$in_file: Generating new uuid" || continue

      set_front_matter_key "$in_file" "uuid" $md_uuid
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
